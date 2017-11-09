DATADIR=../../Data
DLINADIR=../../dlina-project-fork
TEI2XMI="de.unistuttgart.ims.drama.main.TEI2XMI"
ZIPDIR=/mount/www2/htdocs/gcl/reiterns/quadrama/res

MAIN.VERSION=$(xpath -e '/project/version/text()' pom.xml 2>/dev/null)

package:
	mvn clean package

run-es: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/es/ --output ${DATADIR}/xmi/es/ --doCleanup --collectionId "es" --language "es" --skipNER  --readerClassname "de.unistuttgart.quadrama.io.tei.textgrid.CoreTEIUrlReader"

run-tc:
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ../../theatreclassique/tei/ --output ${DATADIR}/xmi/tc/ --doCleanup --collectionId "tc" --language "fr" --skipNER --skipSpeakerIdentifier --readerClassname "de.unistuttgart.quadrama.io.tei.textgrid.TheatreClassicUrlReader"

run-tg: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/tg/ --output ${DATADIR}/xmi/tg/ --dlinaDirectory ${DLINADIR}/data/zwischenformat/ --genderModel src/main/resources/gender-v2.jar  --collectionId "tg" --doCleanup

run-gdc: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ../../gerdracor/data --output ${DATADIR}/xmi/gdc/ --skipSpeakerIdentifier  --collectionId "gdc" --doCleanup --readerClassname "de.unistuttgart.quadrama.io.tei.textgrid.GerDraCorUrlReader"

%.csv.zip:
	cd ${DATADIR}/xmi; zip -r ${ZIPDIR}/$@ . -i $*/*.csv

%.zip:
	cd ${DATADIR}/xmi; zip -r ${ZIPDIR}/$@ . -i $*/*.xmi



commitpush:
	git -C ${DATADIR} add xmi/tg/*.xmi
	git -C ${DATADIR} commit -m "drama.Main ${MAIN.VERSION}"
	git -C ${DATADIR} push
