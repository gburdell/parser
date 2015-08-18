
`ifdef MAC1
//comment
`elsif MAC2
/*
 *block comment
 */
`define D1(a,b,c) (a+b+c)
`else
`ifndef MAC3
module mac3(); endmodule
`endif	//!MAC3
`endif
