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
antBbuilder.path ( 'id' : 'groovy.lib' ) {
    fileset ( 'dir' : '${basedir}' ) {
      include ( 'name' : 'lib/groovy-1.8.2/lib/*.jar' )
    }
  }
antBbuilder.taskdef ( 'name' : 'groovy' , 'classname' : 'org.codehaus.groovy.ant.Groovy' ) {
    classpath ( 'refid' : 'groovy.lib' )
  }
antBbuilder.taskdef ( 'classpathref' : 'cobertura.classpath' , 'resource' : 'tasks.properties' )


def report(){
	antBbuilder.'cobertura-report' ( 'destdir' : '${coveragereport.dir}' , 'srcdir' : '${src.dir}' , 'datafile' : '${datafile}' , 'format' : 'html' )
}


def instrumentWar(){

  antBbuilder.'cobertura-instrument' ( 'todir' : '${instrumented.dir}' , 'datafile' : '${datafile}' ) {
    antBbuilder.includeClasses ( 'regex' : '${include.regex}' )
    antBbuilder.instrumentationClasspath {
      path ( 'refid' : 'test.classpath' )
    }
  }

}


def injectCoberturaWar(){
	def warDir = properties['instrumented.dir']
	def warFile = properties['war.file']

	new AntBuilder().unzip(src:warDir+'/'+warFile, dest:'stagingWAR') 
	new AntBuilder().copy(file:'lib/cobertura-1.9.4.1/cobertura.jar', tofile:'stagingWAR/WEB-INF/lib/cobertura.jar')
	new AntBuilder().zip(destfile:warDir+'/'+warFile, basedir:'stagingWAR')
}


def cleanDataFile (){
  new AntBuilder().delete ( 'file' : '${datafile}' )
}

def cleanInstrumented (){
  new AntBuilder().delete ( 'dir' : '${instrumented.dir}' )
  new AntBuilder().mkdir ( 'dir' : '${instrumented.dir}' )
}

def cleanReport (){
  new AntBuilder().delete ( 'dir' : '${coveragereport.dir}' )
}

def instrument (){
	instrumentWar()
	injectCoberturaWar()
}

cleanReport()