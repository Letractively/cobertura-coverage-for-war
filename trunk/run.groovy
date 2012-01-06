def antBuilder=  new AntBuilder()
antBuilder.property ( 'file' : 'cobertura.properties' )
antBuilder.property ( 'name' : 'project.base' , 'value' : '.' )
antBuilder.property ( 'name' : 'cobertura.dir' , 'value' : './lib/cobertura-1.9.4.1' )
antBuilder.property ( 'name' : 'datafile' , 'value' : 'cobertura.ser' )
antBuilder.path ( 'id' : 'cobertura.classpath' ) {
    fileset ( 'dir' : '${cobertura.dir}' ) {
      include ( 'name' : 'cobertura.jar' )
      include ( 'name' : 'lib/**/*.jar' )
    }
  }
antBuilder.path ( 'id' : 'test.classpath' ) {
    fileset ( 'dir' : '${war.dir}' ) {
      include ( 'name' : '**/${war.file}' )
    }
  }
antBuilder.taskdef ( 'classpathref' : 'cobertura.classpath' , 'resource' : 'tasks.properties' )


def report(antBuilder){
	antBuilder.'cobertura-report' ( 'destdir' : '${coveragereport.dir}' ,  'datafile' : '${datafile}' , 'format' : 'html' ){
	    fileset ( 'dir' : '${src.dir}' ) {
	      include ( 'name' : '**/*.java' )
	    }
	}
	
}


def instrumentWar(antBuilder){

  antBuilder.'cobertura-instrument' ( 'todir' : '${instrumented.dir}' , 'datafile' : '${datafile}' ) {
    includeClasses ( 'regex' : '${include.regex}' )
    instrumentationClasspath {
      path ( 'refid' : 'test.classpath' )
    }
  }

}


def injectCoberturaWar(antBuilder){
	antBuilder.unzip(src:'${instrumented.dir}'+'/${war.file}', dest:'stagingWAR') 
	antBuilder.copy(file:'lib/cobertura-1.9.4.1/cobertura.jar', tofile:'stagingWAR/WEB-INF/lib/cobertura.jar')
	antBuilder.zip(destfile:'${instrumented.dir}/${war.file}', basedir:'stagingWAR')
}


def cleanDataFile (antBuilder){
  antBuilder.delete ( 'file' : '${datafile}' )
}

def cleanInstrumented (antBuilder){
  antBuilder.delete ( 'dir' : '${instrumented.dir}' )
  antBuilder.mkdir ( 'dir' : '${instrumented.dir}' )
}

def cleanReport (antBuilder){
  antBuilder.delete ( 'dir' : '${coveragereport.dir}' )
}

def instrument (antBuilder){
	cleanInstrumented (antBuilder)
	instrumentWar(antBuilder)
	injectCoberturaWar(antBuilder)
}

def command= args[0]


switch (command){
	case 'instrument': instrument (antBuilder)
			break
	case 'report' : report (antBuilder)
		break
	default: println 'Unknown command. usage: groovy run <instrument or report>'

}