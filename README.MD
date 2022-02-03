# ![Fantasy's Dice](./src/main/resources/banner.png)
[![release](https://github.com/ApexModder/FantasyTable/actions/workflows/release.yml/badge.svg)](https://github.com/ApexModder/FantasyTable/actions/workflows/release.yml)
[![license](https://img.shields.io/github/license/ApexModder/FantasyTable)](https://github.com/ApexModder/FantasyTable/blob/master/LICENSE)
[![last commit](https://img.shields.io/github/last-commit/ApexModder/FantasyTable)](https://github.com/ApexModder/FantasyTable/)
[![release](https://img.shields.io/github/v/release/ApexModder/FantasyTable)](https://github.com/ApexModder/FantasyTable/releases)
[![curseforge](https://cf.way2muchnoise.eu/versions/573867.svg)](https://www.curseforge.com/minecraft/mc-mods/fantasys-dice)

[<img src="https://i.imgur.com/qUJzbM4.png" width=33%></img>](http://discord.apexmods.xyz/)

---

Mod requested by **_FantasyGaming_** which adds **_6_** & **_20 Sided Dice_** to the game of varying types.<br>
This mod requires [**_ApexCore_**](https://github.com/ApexModder/ApexCore) & [**_Registrator_**](https://github.com/ApexModder/Registrator/). <br>
The prioritized version for this mod will be for 1.18.1, though 1.16.5 will still be supported but not as priority. <br>

### **_Planned Features_**
(These are subject to change)
- Gold and Iron coins that can be flipped to get either heads or tails

### **_Current Features_**
- `/roll` command to roll in chat with a specified number similar to _World of Warcraft_ (ex: `/roll 100` will always roll out of _100_, with the roll being shown in chat)
- In addition, chat will show how many dice have been rolled (rolling _5 Six-Sided Dice_ will show like this: `<player> rolled <roll> (5d6)`)
- Paper dice that can be dyed to any combination the same way leather armor can
- Colored chat text for each dice (matches paper dice color if used)
- Dice pouches that can also be dyed like leather armor, and have 6x3 spaces in it
- Regular, Cosmetic, and Specialty Dice
- A new crafting station, the Dice Station (works like a stonecutter and is made with a dice pouch and a crafting table)
- JEI Integration for recipes and the Dice Station


### **_Dice Types_**
Each Dice has a Quality Value (Higher value means higher chance to get a bonus to your roll)

- Wood (-2)
- Stone (-1)
- Bone (neutral)
- Iron (neutral)
- Gold (+1)
- Diamond (+2)
- Emerald (+3)
- Netherite (+4)

- Cosmetic Dice
- Specialty Dice

(For those who don't like the quality value option, there is a config to change the values; simply set them to "0")

### **_Cosmetic Dice_**
These dice don't have any quality values, and are purely cosmetic.

- Paper Dice
- Ender Dice
- Frozen Dice
- Slime Dice
- Redstone Dice

### **_Specialty Dice_** (_Requested by Devs_)
- **_Fantasy's Lucky Dice_**
  - _Lucky Rollers_ always roll _High Values_, _Unlucky Rollers_ always roll _Low Values_ (_Configurable_).
  - _Luck_ depends on who rolled the dice.
- **_Tobi's Thrice Dice_**
  - Rolls 3 times when used, result is the largest value of the three rolls.
  - Stacks to maximum of 1.
- **_Apex's NULL Dice_**
  - When rolled value gets _NULLIFIED_ and ends up negative.
- **_Symacon's Gambling Dice_**
  - Has a 50% chance to double the user's roll or a 50% chance to half it.
  - Rolls cap at the dice maximum and minimum.
