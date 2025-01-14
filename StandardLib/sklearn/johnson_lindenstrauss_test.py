"""
=====================================================================
The Johnson-Lindenstrauss bound for embedding with random projections
=====================================================================


The `Johnson-Lindenstrauss lemma`_ states that any high dimensional
dataset can be randomly projected into a lower dimensional Euclidean
space while controlling the distortion in the pairwise distances.

.. _`Johnson-Lindenstrauss lemma`: http://en.wikipedia.org/wiki/Johnson%E2%80%93Lindenstrauss_lemma


Theoretical bounds
==================

The distortion introduced by a random projection `p` is asserted by
the fact that `p` is defining an eps-embedding with good probability
as defined by:

  (1 - eps) ||u - v||^2 < ||p(u) - p(v)||^2 < (1 + eps) ||u - v||^2

Where u and v are any rows taken from a dataset of shape [n_samples,
n_features] and p is a projection by a random Gaussian N(0, 1) matrix
with shape [n_components, n_features] (or a sparse Achlioptas matrix).

The minimum number of components to guarantees the eps-embedding is
given by:

  n_components >= 4 log(n_samples) / (eps^2 / 2 - eps^3 / 3)


The first plot shows that with an increasing number of samples ``n_samples``,
the minimal number of dimensions ``n_components`` increased logarithmically
in order to guarantee an ``eps``-embedding.

The second plot shows that an increase of the admissible
distortion ``eps`` allows to reduce drastically the minimal number of
dimensions ``n_components`` for a given number of samples ``n_samples``


Empirical validation
====================

We validate the above bounds on the the digits dataset or on the 20 newsgroups
text document (TF-IDF word frequencies) dataset:

- for the digits dataset, some 8x8 gray level pixels data for 500
  handwritten digits pictures are randomly projected to spaces for various
  larger number of dimensions ``n_components``.

- for the 20 newsgroups dataset some 500 documents with 100k
  features in total are projected using a sparse random matrix to smaller
  euclidean spaces with various values for the target number of dimensions
  ``n_components``.

The default dataset is the digits dataset. To run the example on the twenty
newsgroups dataset, pass the --twenty-newsgroups command line argument to this
script.

For each value of ``n_components``, we plot:

- 2D distribution of sample pairs with pairwise distances in original
  and projected spaces as x and y axis respectively.

- 1D histogram of the ratio of those distances (projected / original).

We can see that for low values of ``n_components`` the distribution is wide
with many distorted pairs and a skewed distribution (due to the hard
limit of zero ratio on the left as distances are always positives)
while for larger values of n_components the distortion is controlled
and the distances are well preserved by the random projection.


Remarks
=======

According to the JL lemma, projecting 500 samples without too much distortion
will require at least several thousands dimensions, irrespectively of the
number of features of the original dataset.

Hence using random projections on the digits dataset which only has 64 features
in the input space does not make sense: it does not allow for dimensionality
reduction in this case.

On the twenty newsgroups on the other hand the dimensionality can be decreased
from 56436 down to 10000 while reasonably preserving pairwise distances.

"""
import sys
from time import time
import numpy as np
import pylab as pl
from sklearn.random_projection import johnson_lindenstrauss_min_dim
from sklearn.random_projection import SparseRandomProjection
from sklearn.metrics.pairwise import euclidean_distances

# Part 2: perform sparse random projection of some digits images which are
# quite low dimensional and dense or documents of the 20 newsgroups dataset
# which is both high dimensional and sparse

n_samples = 1000
n_features = 65536   # 4096, 8192, 65536
 
t0 = time()
data = np.random.uniform(0,1,(n_samples, n_features))

print("Generating random data in %0.3fs" %(time() - t0)) 

print("Embedding %d samples with dim %d using various random projections"
      % (n_samples, n_features))

n_components_range = np.array([300, 1000, 3000])

lens = np.zeros((n_samples,1))
projected_lens = np.zeros((n_samples,1))
for i in range(n_samples):
    lens[i] = np.linalg.norm(data[i,:])


#dists = euclidean_distances(data, squared=True).ravel()
## select only non-identical samples pairs
#nonzero = dists != 0
#dists = dists[nonzero]

for n_components in n_components_range:
    t0 = time()
    rp = SparseRandomProjection(n_components=n_components)
    projected_data = rp.fit_transform(data)
    print("Projected %d samples from %d to %d in %0.3fs"
          % (n_samples, n_features, n_components, time() - t0))
    if hasattr(rp, 'components_'):
        n_bytes = rp.components_.data.nbytes
        n_bytes += rp.components_.indices.nbytes
        print("Random matrix with size: %0.3fMB" % (n_bytes / 1e6))

    # projected lengths
    for i in range(n_samples):
        projected_lens[i] = np.linalg.norm(projected_data[i,:])    
    rates = projected_lens / lens    
    
    print("Mean distances rate: %0.2f (%0.2f)"
          % (np.mean(rates), np.std(rates)))


#    projected_dists = euclidean_distances(
#        projected_data, squared=True).ravel()[nonzero]
#
#    pl.figure()
#    pl.hexbin(dists, projected_dists, gridsize=100)
#    pl.xlabel("Pairwise squared distances in original space")
#    pl.ylabel("Pairwise squared distances in projected space")
#    pl.title("Pairwise distances distribution for n_components=%d" %
#             n_components)
#    cb = pl.colorbar()
#    cb.set_label('Sample pairs counts')
#
#    rates = projected_dists / dists
#    print("Mean distances rate: %0.2f (%0.2f)"
#          % (np.mean(rates), np.std(rates)))
#
#    pl.figure()
#    pl.hist(rates, bins=50, normed=True, range=(0., 2.))
#    pl.xlabel("Squared distances rate: projected / original")
#    pl.ylabel("Distribution of samples pairs")
#    pl.title("Histogram of pairwise distance rates for n_components=%d" %
#             n_components)
#    pl.show()



    # TODO: compute the expected value of eps and add them to the previous plot
    # as vertical lines / region



