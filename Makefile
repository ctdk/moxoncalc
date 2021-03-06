JAVAC=javac
JAR=jar
sources = $(wildcard src/*.java)
classes = $(sources:.java=.class)
OUTDIR=./lib
JAVACFLAGS=-d $(OUTDIR)
MCVERSION=`git describe --long --always`

all: dir $(classes)

dir: 
	mkdir -p $(OUTDIR)

clean :
	rm -r $(OUTDIR)
	$(RM) *.jar

%.class : %.java
	$(JAVAC) $(JAVACFLAGS) $<

jar:
	$(JAR) cfm moxoncalc-$(MCVERSION).jar Manifest.txt -C $(OUTDIR) .
