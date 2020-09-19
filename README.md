## Dynamite

Dynamite is an example-based schema mapping and data migration prototype.

### Publication

- Yuepeng Wang, Rushi Shah, Abby Criswell, Rong Pan, Isil Dillig.  
  Data Migration using Datalog Program Synthesis. VLDB 2020

### Dependencies

- Java 8
- Souffle 1.5.1
- Z3 4.8.5
- Mongodb 4.0.3

### Build

Dynamite can be built with Ant 1.10.3
```
ant jar
```

### Usage

After building the tool, you can use Dynamite to generate a Datalog program as schema mapping in the following way
```
./dynamite-mapping.sh <src-schema-path> <tgt-schema-path> <src-example-path> <tgt-example-path>
```

For instance, you may try the following command in the root directory of this repo
```
./dynamite-mapping.sh benchmarks/benchmark1/benchmark1_1/SrcSchema.json benchmarks/benchmark1/benchmark1_1/TgtSchema.txt benchmarks/benchmark1/benchmark1_1/SrcExample.json benchmarks/benchmark1/benchmark1_1/TgtExample.txt
```

Dynamite can also migrate data after generating the schema mapping
```
./dynamite-migration.sh <src-schema-path> <tgt-schema-path> <src-example-path> <tgt-example-path> <src-instance-path> <src-instance-schema-path> <output-path>
```

For example, you can try it on a small mock database
```
./dynamite-migration.sh benchmarks/benchmark1/benchmark1_1/SrcSchema.json benchmarks/benchmark1/benchmark1_1/TgtSchema.txt benchmarks/benchmark1/benchmark1_1/SrcExample.json benchmarks/benchmark1/benchmark1_1/TgtExample.txt benchmarks/benchmark1/SrcInstances/review.json benchmarks/benchmark1/SrcInstSchema.json output
```

### Tests

The tests of Dynamite are built and executed with Ant
```
ant test
```
The estimated running time is 5 minutes.

