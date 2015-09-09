SRC_DIR=$(shell pwd)/src
CLASS_DIR=$(shell pwd)/classes
COMPILER=javac

compile:
	mkdir classes
	$(COMPILER) -d ./classes  $(SRC_DIR)/Folder.java $(SRC_DIR)/FolderViewer.java
run:
	java -cp $(CLASS_DIR) FolderViewer

clean: 
	rm -rf classes
	
