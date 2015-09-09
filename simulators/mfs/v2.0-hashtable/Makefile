SRC_DIR=$(shell pwd)/src
CLASS_DIR=$(shell pwd)/classes
COMPILER=javac

compile:
	mkdir classes
	$(COMPILER) -d ./classes  $(SRC_DIR)/Person.java $(SRC_DIR)/NodePointer.java $(SRC_DIR)/MicroFB2.java
run:
	java -cp $(CLASS_DIR) MicroFB2

clean: 
	rm -rf classes
	
