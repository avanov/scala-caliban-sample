# https://www.gnu.org/software/make/manual/html_node/Special-Variables.html
# https://ftp.gnu.org/old-gnu/Manuals/make-3.80/html_node/make_17.html
PROJECT_MKFILE_PATH     := $(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))
PROJECT_MKFILE_DIR      := $(shell cd $(shell dirname $(PROJECT_MKFILE_PATH)); pwd)


PROJECT_ROOT            	:= $(PROJECT_MKFILE_DIR)
PROJECT_LOCAL_UNTRACK_DIR	:= $(PROJECT_ROOT)/.local
PROJECT_BUILD_DIR			:= $(PROJECT_LOCAL_UNTRACK_DIR)/build


build:
	sbt compile

test:
	sbt "Test/test"
