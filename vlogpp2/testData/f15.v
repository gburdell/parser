
//comment
`timescale 1ns / 100ps

`ifdef M1
FAIL1
`else
PASS1
`endif

`define    M1

`ifdef M1
PASS2

`endif

`undefineall

`resetall
