rm -rf gen-javabean src/main/java/de/hska/iwi/bdelab/schema
thrift -r --gen java:beans,hashcode,nocamel src/main/thrift/schema.thrift
sleep 3
mv gen-javabean/de/hska/iwi/bdelab/schema src/main/java/de/hska/iwi/bdelab/
rm -rf gen-javabean
