DATADIR=../../Data
CDATADIR=../../data_
DLINADIR=../../dlina-project-fork
TEI2XMI="de.unistuttgart.ims.drama.main.TEI2XMI"
ZIPDIR=/mount/www2/htdocs/gcl/reiterns/quadrama/res

MAIN.VERSION=$(xpath -e '/project/version/text()' pom.xml 2>/dev/null)

package:
	mvn clean package

run-es: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/es/ --output ${DATADIR}/xmi/es/ --doCleanup --collectionId "es" --language "es" --skipNER --corpus CORETEI

run-tc:
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ../../theatreclassique/tei/ --output ${DATADIR}/xmi/tc/ --csvOutput ${CDATADIR}tc/csv --doCleanup --collectionId "tc" --language "fr" --skipNER --skipSpeakerIdentifier --corpus THEATRECLASSIQUE

run-tg: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/tg/ --output ${DATADIR}/xmi/tg/ --csvOutput ${CDATADIR}tg/csv --dlinaDirectory ${DLINADIR}/data/zwischenformat/ --genderModel src/main/resources/gender-v2.jar  --collectionId "tg" --doCleanup --corpus TEXTGRID

run-gdc: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ../../gerdracor/data --output ${DATADIR}/xmi/gdc/ --csvOutput ${CDATADIR}gdc/csv --skipSpeakerIdentifier  --collectionId "gdc" --doCleanup --corpus GERDRACOR

run-turm:
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ../../Data/turm/ --output ${DATADIR}/xmi/turm/ --csvOutput ${CDATADIR}turm/csv  --collectionId "turm" --doCleanup --corpus TURM

run-qd:
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ../../QuaDramACorpus/tei --csvOutput ${CDATADIR}qd/csv --skipSpeakerIdentifier  --collectionId "qd" --corpus QUADRAMA

%.csv.zip:
	cd ${DATADIR}/xmi; zip -r ${ZIPDIR}/$@ . -i $*/*.csv

%.zip:
	cd ${DATADIR}/xmi; zip -r ${ZIPDIR}/$@ . -i $*/*.xmi



commitpush:
	git -C ${DATADIR} add xmi/tg/*.xmi
	git -C ${DATADIR} commit -m "drama.Main ${MAIN.VERSION}"
	git -C ${DATADIR} push
