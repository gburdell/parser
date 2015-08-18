`define M1

`ifdef M2
FAIL_M2_code
`elsif M3
FAIL_M3_CODE

`ifdef M4
FAIL1
`elsif M1
FAIL2
`endif

FAIL3
`else
PASS_not_M2_code
`endif
