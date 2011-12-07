@Grapes([
 	@Grab('log4j:log4j:1.2.16'),
 	@GrabConfig(systemClassLoader = true)
])

import org.apache.log4j.Logger
import org.apache.log4j.Level


def logger = Logger.getLogger('cobertura')
logger.setLevel(Level.INFO);

logger.info "START"
logger.info "OVER"
