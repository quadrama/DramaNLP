package de.unistuttgart.quadrama.db.neo4j;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONObject;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class Neo4jConsumer extends JCasAnnotator_ImplBase {

	String serverRootURI = "http://localhost:7474/";
	HttpAuthenticationFeature feature;
	ClientConfig clientConfig;
	WebTarget nodeWebTarget;
	Client client;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		clientConfig = new ClientConfig();
		String username = "neo4j";
		String password = "neo4j";
		feature = HttpAuthenticationFeature.basic(username, password);

		JSONObject clearCommand = new JSONObject();
		clearCommand.append("statements", new JSONObject("{statement:\"MATCH (n) DETACH DELETE n\"}"));
		client = ClientBuilder.newClient(clientConfig);
		WebTarget wt = client.target(serverRootURI + "db/data/transaction/commit");
		wt.register(feature);
		Response response = wt.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(clearCommand.toString(), MediaType.APPLICATION_JSON));
		System.out.println(String.format("GET on [%s], status code [%d]", serverRootURI, response.getStatus()));
		response.close();

		String nodeEntryPointUri = serverRootURI + "db/data/node";
		nodeWebTarget = client.target(nodeEntryPointUri);

	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		Map<Annotation, URI> uriMap = new HashMap<Annotation, URI>();
		JSONObject object;
		for (Figure figure : JCasUtil.select(aJCas, Figure.class)) {
			object = new JSONObject();
			object.put("name", figure.getCoveredText());
			object.put("words", figure.getNumberOfWords());
			URI uri = storeInDatabase(object);
			setLabel(uri, "Figure");
			uriMap.put(figure, uri);
		}
		int actOrder = 0;
		for (Scene act : JCasUtil.select(aJCas, Scene.class)) {
			object = new JSONObject();
			object.put("name", StringUtils.substring(act.getCoveredText(), 0, 20));
			object.put("order", actOrder++);
			URI actURI = storeInDatabase(object);
			setLabel(actURI, "Act");

			Set<Figure> figures = new HashSet<Figure>();
			for (Speaker speaker : JCasUtil.selectCovered(Speaker.class, act)) {
				try {
					if (speaker.getFigure() != null)
						figures.add(speaker.getFigure());
				} catch (NullPointerException e) {
				}
			}
			for (Figure figure : figures) {
				URI figureURI = uriMap.get(figure);
				relate(figureURI, actURI, "on_stage");
			}

			if (false)
				for (Utterance utterance : JCasUtil.selectCovered(Utterance.class, act)) {
					object = new JSONObject();
					object.put("text", utterance.getCoveredText());
					URI utteranceURI = storeInDatabase(object);
					setLabel(utteranceURI, "Utterance");
					try {
						Figure fig = DramaUtil.getFigure(utterance);
						relate(uriMap.get(fig), utteranceURI, "utters");
					} catch (NullPointerException e) {
						// matching error
					}
				}

		}

	}

	private int relate(URI uri1, URI uri2, String type) {
		JSONObject obj = new JSONObject();
		obj.put("to", uri2.toASCIIString());
		obj.put("type", type);
		Response r = client.target(uri1.toString() + "/relationships").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(obj.toString(), MediaType.APPLICATION_JSON));
		return r.getStatus();
	}

	private int setLabel(URI uri, String label) {
		Response r = client.target(uri.toString() + "/labels").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity("\"" + label + "\"", MediaType.APPLICATION_JSON));
		return r.getStatus();
	}

	private URI storeInDatabase(JSONObject obj) {
		Response response = nodeWebTarget.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(obj.toString(), MediaType.APPLICATION_JSON));

		return response.getLocation();

	}
}
