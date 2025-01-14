'''
Created on Dec 28, 2012

@author: Nguyen Huu Hiep
'''

#!/usr/bin/python
# File: auto_diff.py
# Author: Vitalii Vanovschi
# Desc: This program demonstrates parallel computations with pp module 
# using class methods as parallel functions (available since pp 1.4).
# Program calculates the partial sums of f(x) = x-x**2/2+x**3/3-x**4/4+... 
# and first derivatives f'(x) using automatic differentiation technique.
# In the limit f(x) = ln(x+1) and f'(x) = 1/(x+1).
# Parallel Python Software: http://www.parallelpython.com

import math, sys
import pp

# Partial implementation of automatic differentiation class
class AD:
    def __init__(self, x, dx=0.0):
        self.x = float(x)
        self.dx = float(dx)

    def __pow__(self, val):
        if isinstance(val, int):
            p = self.x ** val
            return AD(self.x ** val, val * self.x ** (val - 1) * self.dx)
        else:
            raise TypeError("Second argumnet must be an integer")

    def __add__(self, val):
        if isinstance(val, AD):
            return AD(self.x + val.x, self.dx + val.dx) 
        else:
            return AD(self.x + val, self.dx) 
    
    def __radd__(self, val):
        return self +val

    def __mul__(self, val):
        if isinstance(val, AD):
            return AD(self.x * val.x, self.x * val.dx + val.x * self.dx) 
        else:
            return AD(self.x * val, val * self.dx) 

    def __rmul__(self, val):
        return self * val    
    
    def __div__(self, val):
        if isinstance(val, AD):
           return self * AD(1 / val.x, -val.dx / val.x ** 2)
        else:
           return self * (1 / float(val))

    def __rdiv__(self, val):
        return AD(val) / self
    
    def __sub__(self, val):
        if isinstance(val, AD):
            return AD(self.x - val.x, self.dx - val.dx) 
        else:
            return AD(self.x - val, self.dx) 

    def __repr__(self):
        return str((self.x, self.dx))

# This class contains methods which will be executed in parallel
class PartialSum:
    def __init__(self, n):
        self.n = n
    
    #truncated natural logarithm
    def t_log(self, x):
        return self.partial_sum(x - 1)

    #partial sum for truncated natural logarithm
    def partial_sum(self, x):
        return sum([float(i % 2 and 1 or -1) * x ** i / i for i in xrange(1, self.n)])

print """Usage: python auto_diff.py [ncpus]
    [ncpus] - the number of workers to run in parallel, 
    if omitted it will be set to the number of processors in the system
"""

# tuple of all parallel python servers to connect with
ppservers = ()
#ppservers = ("10.0.0.1",)

if len(sys.argv) > 1:
    ncpus = int(sys.argv[1])
    # Creates jobserver with ncpus workers
    job_server = pp.Server(ncpus, ppservers=ppservers)
else:
    # Creates jobserver with automatically detected number of workers
    job_server = pp.Server(ppservers=ppservers)

print "Starting pp with", job_server.get_ncpus(), "workers"

proc = PartialSum(20000)

results = []
for i in range(32):
    # Creates an object with x = float(i)/32+1 and dx = 1.0
    ad_x = AD(float(i) / 32 + 1, 1.0)
    # Submits a job of calulating proc.t_log(x). 
    f = job_server.submit(proc.t_log, (ad_x,))
    results.append((ad_x.x, f))

for x, f in results:
    # Retrieves the result of the calculation
    val = f()
    print "t_log(%lf) = %lf, t_log'(%lf) = %lf" % (x, val.x, x, val.dx)

# Print execution statistics
job_server.print_stats()

# Parallel Python Software: http://www.parallelpython.com
