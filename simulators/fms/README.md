File Manager Simulator
======================

A command line program that simulates the creation,<br>
deletion and traversal over directory nodes.

#### How to Enter commands
Upon executing the program the following will appear in the terminal

```Enter Command:____``` hit enter <br> ```Enter argument:____```
### Commands

Create directory: ```C <directory name>```

Move to subdirectory: ```D <subdirectory name>```

Move to parent directory: ```U```

Remove subdirectory: ```R <subdirectory name>```

List of subdirectories in current directory: ```L```

Print full path of current directory: ```P```

Print the number of subdirectories in current directory: ```N```


Move to directory specified by absolute path:```A <arg_1> ... <arg_n>```<br>
<sub>Note: The very top directory by default is named Root exclude from arguments.<br>
Remember, add one argument at a time until prompted otherwise.</sub>

Move to directory specified by relative path: ```G <arg_1> ... <arg_n>```<br>
<sub>Note: same rules from previous command apply.</sub><br>
<sub> This command can take the ```UP``` argument which is equivalent to ```../``` in terminal</sub>
    





