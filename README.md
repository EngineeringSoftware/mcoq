# mCoq: Mutation Analysis for Coq

mCoq is a tool for mutation analysis of verification projects that use the
[Coq proof assistant](https://coq.inria.fr).

mCoq applies a set of mutation operators to Coq definitions, generating
modified versions, called mutants, of the project. If all proofs of a
mutant are successfully checked, a mutant is declared live; otherwise it
is declared killed. mCoq produces HTML reports pinpointing both live and
killed mutants in the Coq code, where live mutants may indicate
incomplete specifications. The [research paper][ase-paper] provides more
information on the technique and optimizations that mCoq implements.

Note to ICSE-Demo reviewers: we recently cleaned up the code and improved
our scripts for running the tool, so the steps shown in the paper submission
may differ from the ones below. Additionally, the directory structure
may differ from the one shown in the demo video.

[ase-paper]: https://users.ece.utexas.edu/~gligoric/papers/CelikETAL19mCoq.pdf

## Requirements

- [OCaml 4.07.1](https://ocaml.org)
- [Coq 8.10.2](https://coq.inria.fr/download)
- [SerAPI 0.7.0](https://github.com/ejgallego/coq-serapi)
- [Python 3](https://www.python.org)
- [JDK 8](https://openjdk.java.net) (or later)
- [Gradle 6](https://gradle.org/install/)
- [Git](https://git-scm.com)

## Installation

We strongly recommend installing the required versions of OCaml, Coq,
and SerAPI via the [OPAM package manager](https://opam.ocaml.org),
version 2.0.5 or later.

To set up the OPAM-based OCaml environment, use:
```
opam switch create 4.07.1
opam switch 4.07.1
eval $(opam env)
```
Then, install Coq and SerAPI, pinning them to avoid unintended upgrades:
```
opam update
opam pin add coq 8.10.2
opam pin add coq-serapi 8.10.0+0.7.0
```
Next, clone the mCoq repository and enter the directory:
```
git clone https://github.com/EngineeringSoftware/mcoq.git
cd mcoq
```

The entry point for using mCoq is the `mcoq.py` script. To see
the available options, use:
```
./mcoq.py --help
```

Note that the tool currently assumes that all project Coq source
files to be mutated are listed in the `_CoqProject` file
in the project's root directory.

## Usage example

To apply mCoq to [StructTact][structtact-repo]
revision [`82a85b7`][structtact-revision], run:
```
./mcoq.py --project StructTact --threads 2 --sha 82a85b7 \
  --url https://github.com/uwplse/StructTact.git \
  --buildcmd "./configure && make -j2" --qdir ".,StructTact"
```
After running this command, the HTML report is available in the
`reports/results/StructTact` directory. We also provide the corresponding report
for [online viewing][structtact-report].

For large Coq projects, we recommend setting the `--threads` option
to at least the number of CPU cores in the machine, since mutation analysis
may otherwise take a very long time to complete.

[structtact-repo]: https://github.com/uwplse/StructTact
[structtact-revision]: https://github.com/uwplse/StructTact/commit/82a85b7ec07e71fa6b30cfc05f6a7bfb09ef2510
[structtact-report]: https://cozy.ece.utexas.edu/mcoq/report/

## Authors

- [Ahmet Celik](https://ahmet-celik.github.io)
- [Emilio Jesús Gallego Arias](https://www.irif.fr/~gallego/)
- [Milos Gligoric](http://users.ece.utexas.edu/~gligoric/)
- Kush Jain
- [Karl Palmskog](https://setoid.com)
- Marinela Parovic

## Acknowledgements

The mCoq developers thank Arthur Charguéraud, Georges Gonthier, Farah Hariri, Cătălin Hrițcu,
Robbert Krebbers, Pengyu Nie, Zachary Tatlock, James R. Wilcox and Théo Zimmermann
for their feedback on this work.
