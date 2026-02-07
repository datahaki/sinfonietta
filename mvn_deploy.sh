#!/bin/bash

set -e

(cd ../../tensor; mvn install -Dmaven.test.skip=true)
(cd ../../bridge; mvn install -Dmaven.test.skip=true)
(cd ../../sophus; mvn install -Dmaven.test.skip=true)

(cd ../midi; mvn install -Dmaven.test.skip=true)
(cd ../notation; mvn install -Dmaven.test.skip=true)

mvn clean deploy

