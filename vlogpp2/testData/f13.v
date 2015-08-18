
/* simple test of `protect */

/*allow prefix comment*/ `protect
iutpiueripur
gsfosig[fgis[fg
fgsfosgi[fig[
ggf[gif[si
`endprotect
PASS1
PASS2
`ifdef M1
FAIL1
`else
PASS3
`endif
