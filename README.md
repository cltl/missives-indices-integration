## CLARIAH+ VOC Use Case: Preprocessing and Indices Integration

This repository contains code for the preprocessing of the VOC missives. Specifically, we aimed here at utilizing named entity annotations from the missives' indices to preannotate the data for Named-Entity annotation with Inception.

The code:
- inputs missives in text format (prealably extracted from TEI files)
1. tokenizes the text using a rule-based tokenizer
2. identifies matching named entities in input XML files (where the entities were extracted from the missives' indices)
- outputs the tokenized text and named entities in XMI format, to input to Inception

### Note and Future development

The code for this repository was written between 27/02/20 and 02/03/20 for the most part, with updates on 20/03/20 and 21/03/20. 

A more recent repository, [voc-missives](https://github.com/cltl/voc-missives), preprocesses the missives directly from TEI files (functionality #1: tokenization). We plan to integrate the entity-matching functionality presented here (functionality #2) in that repository next.
