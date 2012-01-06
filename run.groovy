def antBbuilder=  new AntBuilder()
antBbuilder.property ( 'file' : 'cobertura.properties' )
antBbuilder.property ( 'name' : 'project.base' , 'value' : '.' )
antBbuilder.property ( 'name' : 'cobertura.dir' , 'value' : './lib/cobertura-1.9.4.1' )
antBbuilder.property ( 'name' : 'datafile' , 'value' : 'cobertura.ser' )
antBbuilder.path ( 'id' : 'cobertura.classpath' ) {
    fileset ( 'dir' : '${cobertura.dir}' ) {
      include ( 'name' : 'cobertura.jar' )
      include ( 'name' : 'lib/**/*.jar' )
    }
  }
antBbuilder.path ( 'id' : 'test.classpath' ) {
    fileset ( 'dir' : '${war.dir}' ) {
      include ( 'name' : '**/${war.file}' )
    }
  }
antBbuilder.taskdef ( 'classpathref' : 'cobertura.classpath' , 'resource' : 'tasks.properties' )


def report(antBbuilder){
	antBbuilder.'cobertura-report' ( 'destdir' : '${coveragereport.dir}' , 'srcdir' : '${src.dir}' , 'datafile' : '${datafile}' , 'format' : 'html' )
}


def instrumentWar(antBbuilder){

  antBbuilder.'cobertura-instrument' ( 'todir' : '${instrumented.dir}' , 'datafile' : '${datafile}' ) {
    includeClasses ( 'regex' : '${include.regex}' )
    instrumentationClasspath {
      path ( 'refid' : 'test.classpath' )
    }
  }

}


def injectCoberturaWar(antBbuilder){
	antBbuilder.unzip(src:'${instrumented.dir}'+'/${war.file}', dest:'stagingWAR') 
	antBbuilder.copy(file:'lib/cobertura-1.9.4.1/cobertura.jar', tofile:'stagingWAR/WEB-INF/lib/cobertura.jar')
	antBbuilder.zip(destfile:'${instrumented.dir}/${war.file}', basedir:'stagingWAR')
}


def cleanDataFile (antBbuilder){
  antBbuilder.delete ( 'file' : '${datafile}' )
}

def cleanInstrumented (antBbuilder){
  antBbuilder.delete ( 'dir' : '${instrumented.dir}' )
  antBbuilder.mkdir ( 'dir' : '${instrumented.dir}' )
}

def cleanReport (antBbuilder){
  antBbuilder.delete ( 'dir' : '${coveragereport.dir}' )
}

def instrument (antBbuilder){
	cleanInstrumented (antBbuilder)
	instrumentWar(antBbuilder)
	injectCoberturaWar(antBbuilder)
}

def command= args[0]


switch (command){
	case 'instrument': instrument (antBbuilder)
			break
	case 'report' : report (antBbuilder)
		break
	default: println 'Unknown command. usage: groovy run <instrument or report>'

}