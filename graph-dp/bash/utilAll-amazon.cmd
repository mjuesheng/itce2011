
echo AMAZON
rem python ../compare/utility_measure_igraph.py ../data/com_amazon_ungraph.gr 20 0 334863 > ../console2/com_amazon_ungraph-UTIL.txt

echo AMAZON-entropy
rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_entropy_0001_2_001.out 20 0 334863 > ../console2/com_amazon_ungraph_entropy_0001_2_001-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_entropy_001_2_001.out 20 0 334863 > ../console2/com_amazon_ungraph_entropy_001_2_001-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_entropy_01_2_001.out 20 0 334863 > ../console2/com_amazon_ungraph_entropy_01_2_001-UTIL.txt

echo AMAZON-cvxopt-nb
rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_200000_20_nb_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_200000_20_nb_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_400000_20_nb_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_400000_20_nb_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_600000_20_nb_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_600000_20_nb_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_800000_20_nb_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_800000_20_nb_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1000000_20_nb_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_1000000_20_nb_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1200000_20_nb_missing.out 5 0 334863 > ../console/com_amazon_ungraph_cvxopt_1200000_20_nb_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1400000_20_nb_missing.out 5 0 334863 > ../console/com_amazon_ungraph_cvxopt_1400000_20_nb_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1600000_20_nb_missing.out 5 0 334863 > ../console/com_amazon_ungraph_cvxopt_1600000_20_nb_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1800000_20_nb_missing.out 5 0 334863 > ../console/com_amazon_ungraph_cvxopt_1800000_20_nb_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_2000000_20_nb_missing.out 5 0 334863 > ../console/com_amazon_ungraph_cvxopt_2000000_20_nb_missing-UTIL.txt

echo AMAZON-cvxopt-rand
rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_200000_20_rand_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_200000_20_rand_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_400000_20_rand_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_400000_20_rand_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_600000_20_rand_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_600000_20_rand_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_800000_20_rand_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_800000_20_rand_missing-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1000000_20_rand_missing.out 20 0 334863 > ../console2/com_amazon_ungraph_cvxopt_1000000_20_rand_missing-UTIL.txt

echo AMAZON-switch-rand
rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_rand_100000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_rand_100000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_rand_200000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_rand_200000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_rand_300000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_rand_300000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_rand_400000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_rand_400000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_rand_500000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_rand_500000-UTIL.txt

echo AMAZON-switch-nb
rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_nb_100000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_nb_100000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_nb_200000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_nb_200000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_nb_300000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_nb_300000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_nb_400000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_nb_400000-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../switch/com_amazon_ungraph_switch_nb_500000.out 20 1 334863 > ../console2/com_amazon_ungraph_switch_nb_500000-UTIL.txt

echo AMAZON-randwalk
rem python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_2_10.out 20 2 334863 > ../console2/com_amazon_ungraph_randwalk_2_10-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_3_10.out 20 2 334863 > ../console2/com_amazon_ungraph_randwalk_3_10-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_5_10.out 20 2 334863 > ../console2/com_amazon_ungraph_randwalk_5_10-UTIL.txt

rem python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_10_10.out 20 2 334863 > ../console2/com_amazon_ungraph_randwalk_10_10-UTIL.txt

echo AMAZON-randwalk-replace
python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_replace_2_10_0.2.out 20 2 334863 > ../console/com_amazon_ungraph_randwalk_replace_2_10_0.2-UTIL.txt

python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_replace_2_10_0.4.out 20 2 334863 > ../console/com_amazon_ungraph_randwalk_replace_2_10_0.4-UTIL.txt

python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_replace_2_10_0.6.out 20 2 334863 > ../console/com_amazon_ungraph_randwalk_replace_2_10_0.6-UTIL.txt

python ../compare/utility_measure_igraph.py ../randwalk/com_amazon_ungraph_randwalk_replace_2_10_0.8.out 20 2 334863 > ../console/com_amazon_ungraph_randwalk_replace_2_10_0.8-UTIL.txt

echo AMAZON-cvxopt-rdwalk
python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_200000_20_rw_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_200000_20_rw_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_400000_20_rw_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_400000_20_rw_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_600000_20_rw_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_600000_20_rw_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_800000_20_rw_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_800000_20_rw_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1000000_20_rw_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_1000000_20_rw_missing-UTIL.txt

echo AMAZON-cvxopt-rdwalk-first
python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_200000_20_rw_first_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_200000_20_rw_first_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_400000_20_rw_first_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_400000_20_rw_first_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_600000_20_rw_first_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_600000_20_rw_first_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_800000_20_rw_first_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_800000_20_rw_first_missing-UTIL.txt

python ../compare/utility_measure_igraph.py ../out/com_amazon_ungraph_cvxopt_1000000_20_rw_first_missing.out 20 0 334863 > ../console/com_amazon_ungraph_cvxopt_1000000_20_rw_first_missing-UTIL.txt


echo AMAZON-randwalk-keep
rem python ../compare/utility_measure_multigraph.py ../randwalk/com_amazon_ungraph_randwalk_keep_2_0.5.out 5 2 334863 > ../console/com_amazon_ungraph_randwalk_keep_2_0.5-UTIL.txt

rem python ../compare/utility_measure_multigraph.py ../randwalk/com_amazon_ungraph_randwalk_keep_3_0.5.out 5 2 334863 > ../console/com_amazon_ungraph_randwalk_keep_3_0.5-UTIL.txt

rem python ../compare/utility_measure_multigraph.py ../randwalk/com_amazon_ungraph_randwalk_keep_5_0.5.out 5 2 334863 > ../console/com_amazon_ungraph_randwalk_keep_5_0.5-UTIL.txt

rem python ../compare/utility_measure_multigraph.py ../randwalk/com_amazon_ungraph_randwalk_keep_10_0.5.out 5 2 334863 > ../console/com_amazon_ungraph_randwalk_keep_10_0.5-UTIL.txt

