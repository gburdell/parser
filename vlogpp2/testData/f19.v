`define D(x,y) initial $display("start", x , y, "end");
`D( "msg1" , "msg2" )
// expands to 'initial $display("start", "msg1" , "msg2", "end");'
`D( " msg1", )
// expands to 'initial $display("start", " msg1" , , "end");'
`D(, "msg2 ")
// expands to 'initial $display("start", , "msg2 ", "end");'
`D(,)
// expands to 'initial $display("start", , , "end");'
`D( , )
// expands to 'initial $display("start", , , "end");'
`D("msg1")
// illegal, only one argument
