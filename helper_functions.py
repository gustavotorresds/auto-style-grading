import os

# dirpath, dirnames, filenames = 
# print([d for d in walk('./data/files/1247'))

root = './data/files/1363'

f = []
for (dirpath, dirnames, filenames) in os.walk(root):
	f.extend(dirnames)
	break

for submission in f:
	# print(submission)
	l = os.listdir(root + '/' + submission)
	# print(l)
	if 'Breakout.java' not in l:
		print(submission)