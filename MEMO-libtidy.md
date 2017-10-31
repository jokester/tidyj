
## Overall flow of (parse - tidy - output) flow

```text
- DocParseStream(doc, in)
    - called by all public parse APIs: tidyParse{File,Stdin,String,Buffer,Source}
    - calls ParseXMLDocument or ParseDocument
    - returns tidyDocStatus(doc)

- ParseDocument


-
```

## Data structore

```text
- TidyDoc
    - #errors
    - #warnings
    - Node *root
    - options

- Node
    - a linked list of attributes
    - a linked list of children
    -
```
