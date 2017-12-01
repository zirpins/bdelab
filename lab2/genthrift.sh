rm -rf gen-javabean src/main/java/de/hska/iwi/bdelab/schema2
thrift -r --gen java:beans,hashcode,nocamel src/main/thrift/schema2.thrift
sleep 3
mv gen-javabean/de/hska/iwi/bdelab/schema2 src/main/java/de/hska/iwi/bdelab/
rm -rf gen-javabean
