PathOfExile
===========

Apr-Jul 2013. Bot for Path Of Exile run via computer vision.

As an exercise in a field I had little experience with, I decided to tackle automating the play of the Action RPG game, Path of Exile (http://www.pathofexile.com/). I chose to forgo 'injection' methods (altering the binary of the executable) and instead relied purely on screenshots to interact with the game.


Capabilities:

Combat in several arenas (particularly optimized for Forest, Crossroads, Twilight Strands), including optimal targeting, spellcasting, healing and item pickup. Multiple settings possible to prioritize different objectives, including farming or leveling.
Able to navigate the first three home bases: sell, bank, navigate.
Unexpectedly high reliability rate: got stuck perhaps once every ten minutes, forcing the bot to log out and thereby reset its location.


The program itself:

Mostly in Java, and fairly long at about 20k lines of code. There's also a bit of AutoHotkey scripting run from Java and C (Windows API code) bridged through JNI, both for interacting with the window itself.


Methods:

Analyzing a 3-dimensional game at real-time speeds was quite a challenge, and usually required creative methods that were computationally cheap. Most of the vision was done via basic filtering, thresholding, and statistical analysis of processed pixels and images. Much effort was spent to make these techniques resistant to noise and updates to the game, and most code was resilient to these changes, although since then I'm sure the game has changed enough to make this bot obsolete without updates.


Results:

Eventually the bot started to perform a bit too effectively and was caught by the administrators of Path of Exile and was banned. The largest drawback of the bot was that each account had to be hand-built up to a certain point, a drawback that other botting competitors did not face, as injection botting is much easier to expand after establishing a direct API, and can bot building accounts from the ground up. I realized it was simply not worth running the bot after that point, especially against the headwinds of anti-bot systems and thus the project was discontinued.
