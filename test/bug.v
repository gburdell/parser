`define ON
`define OFF

module m3;
assign w =  ( A== 1 ) ? 3'd1:
`OFF
//comment
( C== 2 ) ? 3'd3: 3'd0;
`ON
endmodule
