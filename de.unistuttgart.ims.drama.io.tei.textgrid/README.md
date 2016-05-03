# README


## XPath collection

- 1.xml
  - Act: `TEI/text/body/div`
  - Heading: `div/desc/title`
  - Scene: `div[type=scene]`
- 2.xml
  - Act: `TEI/text/body/div`
  - Heading: `head`
  - Scene: `div`
- 3.xml
  - Act: `TEI/text/body/div:gt(0)` 
  first div contains dramatis personae
  - Heading: `div/desc/title`
  - Scene: `div[type=scene]`
- 4.xml
  - Act: `TEI/text/body/div`
  - Heading: `div/desc/title`
  - Scene: `div[type=scene]`
- 5.xml
  Act: `TEI/text/body/div:gt(0)`
  first div contains dramatis personae
  - Heading: `div/desc/title`
  - Scene: No scene layer
- t4rs.0
  Act: `TEI/text/body/div:gt(0)`
  first div contains dramatis personae
  - Heading: `div/desc/title`
  - Scene: `div:gt(0)`

