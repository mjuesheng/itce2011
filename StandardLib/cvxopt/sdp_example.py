'''
Created on Apr 16, 2013

@author: Nguyen Huu Hiep
'''

from cvxopt import matrix, solvers

c = matrix([1.,-1.,1.])
G = [ matrix([[-7., -11., -11., 3.],
                  [ 7., -18., -18., 8.],
                  [-2.,  -8.,  -8., 1.]]) ]
G += [ matrix([[-21., -11.,   0., -11.,  10.,   8.,   0.,   8., 5.],
                   [  0.,  10.,  16.,  10., -10., -10.,  16., -10., 3.],
                   [ -5.,   2., -17.,   2.,  -6.,   8., -17.,  -7., 6.]]) ]
h = [ matrix([[33., -9.], [-9., 26.]]) ]
h += [ matrix([[14., 9., 40.], [9., 91., 10.], [40., 10., 15.]]) ]
sol = solvers.sdp(c, Gs=G, hs=h)
print(sol['x'])
#[-3.68e-01]
#[ 1.90e+00]
#[-8.88e-01]
print(sol['zs'][0])
#[ 3.96e-03 -4.34e-03]
#[-4.34e-03  4.75e-03]
print(sol['zs'][1])
#[ 5.58e-02 -2.41e-03  2.42e-02]
#[-2.41e-03  1.04e-04 -1.05e-03]
#[ 2.42e-02 -1.05e-03  1.05e-02]