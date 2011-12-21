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


def ant = new AntBuilder()
def PATH = 'cobertura.path'
ant.path(id:PATH){
    fileset(dir:"lib/cobertura-1.9.4.1") {
	 include(name:"cobertura.jar")
	 include(name:"lib/**/*.jar")
    }
}
ant.taskdef(resource:"tasks.properties", classpathref:PATH)


ant."cobertura-report" (format:"html", destdir:"./report" ,srcdir="/my/sourcecode/nexus/src/main/java" ,datafile ="nexus.ser" )

logger.info "OVER"
