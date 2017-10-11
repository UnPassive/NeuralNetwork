import sys
import decimal
import argparse
import random
import math

def twoN():
    for x in frange(decimal.Decimal('-1.5'),3, decimal.Decimal('0.1')):
        for y in frange(decimal.Decimal('-1.5'),3, decimal.Decimal('0.1')):
            fvalue = math.pow((decimal.Decimal('1.0') - x), 2.0) + 100.0*math.pow((y - decimal.Decimal(math.pow(x, 2.0))), 2.0)
            print('%f %f %f' % (x,y,fvalue))
            
def genData(num_points, dim, file):
    incrementor = num_points
    f = open(file, 'w')
    while(int(incrementor) > 0):
        listOfNumbers = []
        for x in range(0, int(dim)):
            listOfNumbers.append(round(random.uniform(-1.5, 3.5), 2))
        sum = 0
        for x in range(0, len(listOfNumbers)-1):
            sum += math.pow((decimal.Decimal('1.0') - decimal.Decimal(listOfNumbers[x])), 2.0) + 100.0*math.pow((decimal.Decimal(listOfNumbers[x+1]) - decimal.Decimal(math.pow(listOfNumbers[x], 2.0))), 2.0)
        incrementor -= 1
        inputs = '\t'.join(str(p) for p in listOfNumbers) + '\t'+ str(round(sum, 2)) + '\n'
        f.write(inputs)
    f.close()

def frange(x,y,jump):
    while x<=y:
        yield x
        x += jump

if __name__ == '__main__':
    parser =  argparse.ArgumentParser(description='N-dimensional data generator for Rosenbrock function.')
    parser.add_argument('num_points', help='Number of datapoints.', type=int)
    parser.add_argument('dim', help='Dimensionality of the input vector.', type=int)
    parser.add_argument('file', help='File name for output file.', type=str)
    args = parser.parse_args()

    genData(args.num_points, args.dim, args.file)
