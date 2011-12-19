@Grapes([
 	@Grab('log4j:log4j:1.2.16'),
 	@GrabConfig(systemClassLoader = true)
])

import org.apache.log4j.Logger
import org.apache.log4j.Level


def logger = Logger.getLogger('cobertura')
logger.setLevel(Level.INFO);

logger.info "START"


dataFile = "cobertura.ser"

def defineCoberturaPathAndTasks() {
    ant.taskdef(classpathRef: 'cobertura.classpath', resource: "tasks.properties")
}

logger.info "OVER"
