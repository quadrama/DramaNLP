#! /usr/bin/env python
# -*- coding: utf-8 -*-

"""
The script validates an input xml/tei-file against an input iso-schematron
schema, using lxml. Prints 'True' to the console if the document is valid,
'False' and the corresponding lxml-Exception otherwise.

Usage: validate_schematron.py schema-file xml/tei-file
"""

from lxml import etree
from lxml import isoschematron
from sys import argv

schematron_inputfile = argv[1]
tei_inputfile = argv[2]

with open(schematron_inputfile, "r") as fh:
    schematron_doc = etree.parse(fh)

schematron = isoschematron.Schematron(schematron_doc)

with open(tei_inputfile, "r") as fh:
    tei_doc = etree.parse(fh)

print(schematron.validate(tei_doc))

try:
    schematron.assertValid(tei_doc)
except Exception as e:
    print(e)
