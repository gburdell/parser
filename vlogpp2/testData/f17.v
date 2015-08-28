`define MACRO_USAGE macro usage
`define M1(_a,_b,_c=5)  _a+_b-_c 
`define M2(_d,_e) always @(_d) _e <= 1'b0

`MACRO_USAGE here and there
`M1(p1+4,p2) and used here
`M2     (p3,p4)
`PIx2	//not defined error
