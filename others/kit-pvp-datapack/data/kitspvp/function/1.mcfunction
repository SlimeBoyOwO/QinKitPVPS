#狙击手
execute as @e[type=arrow,nbt={inGround:0b}] at @s if entity @s[tag=Sniper_Ammo] run particle dust{color:[1.000,0.000,0.000],scale:1} ~ ~ ~ 0 0 0 0 5 force
execute as @e[tag=Sniper_Ammo] run scoreboard players add @s reload 1
execute as @e[tag=Sniper_Ammo] if entity @s[scores={reload=80..}] run kill @s

execute as @e[type=arrow,nbt={inGround:0b}] at @s if entity @s[tag=Love_Arrow] run particle minecraft:cherry_leaves ~ ~ ~ 0.2 0.2 0.2 0.2 5 force
execute as @a[tag=Loved_Sniper] at @s run scoreboard players add @s passiveReload 1
execute as @a[tag=Loved_Sniper] at @s if entity @s[scores={passiveReload=100..}] run tag @s add Pre_Loved_Sniper
execute as @a[tag=Loved_Sniper] at @s if entity @s[scores={passiveReload=2}] run playsound minecraft:entity.allay.death player @a ~ ~ ~ 1 1.8
execute as @a[tag=Loved_Sniper] at @s run particle minecraft:cherry_leaves ~ ~3 ~ 1 1 1 0 1 normal
execute as @a[tag=Loved_Sniper] at @s run particle minecraft:heart ~ ~2.2 ~ 0.2 0.2 0.2 0 1 normal
execute as @a[tag=Pre_Loved_Sniper] run scoreboard players set @s passiveReload 0
execute as @a[tag=Pre_Loved_Sniper] run tag @s remove Loved_Sniper
execute as @a[tag=Pre_Loved_Sniper] run tag @s remove Pre_Loved_Sniper
execute as @e[tag=Love_Arrow] run scoreboard players add @s reload 1
execute as @e[tag=Love_Arrow] if entity @s[scores={reload=80..}] run kill @s

#末影人
execute as @a[tag=Teleporter] at @s run effect clear @s minecraft:darkness
execute as @a[tag=Teleporter] at @s run effect clear @s minecraft:blindness

#堡垒
execute as @a[tag=Fort] run item replace entity @s hotbar.1 with bow[custom_name='"炮塔"',lore=['"感受要塞的怒火吧！"'],enchantments={levels:{"minecraft:knockback":2,"minecraft:flame":1,"minecraft:punch":2,"minecraft:infinity":1}},attribute_modifiers=[{id:"armor",type:"generic.movement_speed",amount:-100.0d,operation:"add_value",slot:"offhand"},{id:"armor",type:"generic.armor",amount:-2.0d,operation:"add_value",slot:"mainhand"},{id:"armor",type:"generic.armor",amount:6.0d,operation:"add_value",slot:"offhand"},{id:"armor",type:"generic.movement_speed",amount:0.02d,operation:"add_value",slot:"mainhand"}]] 1
execute as @a[tag=Fort] run item replace entity @s armor.head with golden_helmet[enchantments={levels:{"minecraft:blast_protection":2,"minecraft:thorns":1}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:2.0d,operation:"add_value",slot:"head"}]] 1
execute as @a[tag=Fort] run tag @s remove Fort
execute as @a[tag=Fort_Extra] if entity @s[tag=Fort_Overload] run scoreboard players add @s reload 1
execute as @a[tag=Fort_Extra] at @s if entity @s[tag=Fort_Overload] run particle soul ~ ~1 ~ 0.5 0.5 0.5 0 1 normal
execute as @a[tag=Fort_Extra] if entity @s[scores={reload=120..}] run tag @s remove Fort_Overload
execute as @a[tag=Fort_Extra] if entity @s[scores={reload=120..}] run scoreboard players set @s reload 0

#宇航员
execute as @a[tag=Astronaut] run item replace entity @s armor.head with player_head[custom_name='"Astronaut Helmet"',profile={id:[I;1510592543,1288063441,-1196701397,1801344907],properties:[{name:"textures",value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE5MmNiNThhODE3MTAwYzEzZTNlY2Q5YWNiODZkMjY1NGM3MjJjNTg1NDU1YTAzYWFmYmNiYWU2NDQwMDliMCJ9fX0="}]}] 1
execute as @e[tag=Astronaut_Bullet] run scoreboard players add @s reload 1
execute as @e[tag=Astronaut_Bullet] if entity @s[scores={reload=300..}] run kill @s

#战争狂人
execute as @a[tag=Reaper_Falling] at @s run particle lava ~ ~ ~ 0 0 0 0 2 normal

#魅魔
execute as @a[tag=Fallen_Love] at @s run particle heart ~ ~1 ~ 0.8 1 0.8 0 1 normal
execute as @e[tag=Fallen_Love] run scoreboard players remove @s passiveReload 1
execute as @e[tag=Fallen_Love] if entity @s[scores={passiveReload=..0}] run tag @s remove Fallen_Love
execute as @e[tag=Fallen_Love] at @s if entity @s[scores={passiveReload=140..}] run particle lava ~ ~2 ~ 0 0 0 0 1 normal
execute as @e[tag=Fallen_Love] at @s run particle heart ~ ~1 ~ 1 1 1 0 1 normal
execute as @e[tag=Love_Kiss] at @s run teleport @s ^ ^ ^1
execute as @e[tag=Love_Kiss] at @s run particle heart ~ ~ ~ 0 0 0 0 5 force
execute as @e[tag=Love_Kiss] run scoreboard players add @s reload 1
execute as @e[tag=Love_Kiss] if entity @s[scores={reload=80..}] run kill @s
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run damage @e[limit=1,sort=nearest,distance=1..3,tag=!LoveDemo_D] 6 dragon_breath by @s
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run effect give @e[limit=1,sort=nearest,distance=1..3,tag=!LoveDemo_D] slowness 3 1
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run effect give @e[limit=1,sort=nearest,distance=1..3,tag=!LoveDemo_D] weakness 3 0
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run effect give @e[limit=1,sort=nearest,distance=1..3,tag=!LoveDemo_D] nausea 3 1
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run tag @e[distance=1..3,limit=1,sort=nearest,tag=!LoveDemo_D] add Fallen_Love
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run scoreboard players set @e[distance=1..3,limit=1,sort=nearest,tag=!LoveDemo_D] passiveReload 160
execute as @e[tag=Love_Kiss] at @s if entity @e[tag=!Love_Kiss,distance=0..2,tag=!LoveDemo_D] run kill @s

#鬼魂
execute as @a[tag=Ghost] at @s if entity @a[tag=Ghost,distance=1..6] run effect give @s glowing 1 1
execute as @a[tag=Ghost_Shot] run scoreboard players add @s reload 1
execute as @a[tag=Ghost] if entity @s[scores={reload=100..}] run effect give @s invisibility infinite 1 true
execute as @a[tag=Ghost] if entity @s[scores={reload=100..}] run tag @s remove Ghost_Shot
execute as @a[tag=Ghost] if entity @s[scores={reload=100..}] run scoreboard players set @s reload 0


#僵尸
execute as @e[tag=Summoned_Zombie] at @s run particle totem_of_undying ~ ~1 ~ 0.5 0.5 0.5 0.5 1 normal
execute as @e[tag=Zombie_passive] at @s run effect give @a[distance=0..1,tag=!Zombie_passive] minecraft:hunger 2 0

#风锤
execute as @e[tag=Wind_Man] if entity @s[scores={Kills_S=1..}] run give @s minecraft:wind_charge 10

#寻声者
execute as @a at @s if entity @a[distance=1..8,tag=Founder] run particle end_rod ~ ~ ~ 0 0 0 0.05 2 force @a[distance=1..16,tag=Founder]
execute as @a[tag=Founder] run scoreboard players add @s reload 1
execute as @a[tag=Founder_D] if entity @s[scores={reload=400..}] run tag @s remove Founder
execute as @a[tag=Founder_D] if entity @s[scores={reload=400..}] run scoreboard players set @s reload 0
execute as @a[tag=Founder] at @s if entity @s[scores={reload=2}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=40}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=80}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=120}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=160}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=200}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=240}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=280}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=320}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1
execute as @a[tag=Founder] at @s if entity @s[scores={reload=360}] run playsound minecraft:entity.warden.heartbeat player @a ~ ~ ~ 1 1

#空袭弩手
execute as @a[tag=Sky_I] run give @s firework_rocket[fireworks={flight_duration:1,explosions:[{shape:"small_ball",colors:[I;11743532]}]}] 1
execute as @a[tag=Sky_I] run tag @s remove Sky_I
execute as @e[tag=Sky,type=arrow,nbt={inGround:1b}] run kill @s
execute as @e[tag=Sky,type=spectral_arrow,nbt={inGround:1b}] run kill @s
execute as @a[tag=Sky] if entity @s[scores={reload=4}] run tag @s add UNFLY
execute as @a[tag=Sky] if entity @s[scores={reload=5}] run tag @s remove UNFLY
execute as @a[tag=Sky] if entity @s[scores={reload=5}] run scoreboard players set @s reload 0

#黑洞
execute as @a[tag=BlackHole_G] run item replace entity @s container.0 with shield[custom_name='"§8午夜凋零"',enchantments={levels:{"minecraft:thorns":15}},attribute_modifiers=[{id:"armor",type:"generic.movement_speed",amount:0.15d,operation:"add_multiplied_base",slot:"mainhand"},{id:"armor",type:"generic.armor",amount:6.0d,operation:"add_value",slot:"offhand"}]] 1
execute as @a[tag=BlackHole_G] run tag @s remove BlackHole_G
execute as @a[tag=BlackHole] at @s run effect give @a[distance=1..3,tag=!Blackhole] poison 5 0
execute as @a[tag=BlackHole] if entity @s[nbt={Inventory:[{id:"minecraft:stone_sword",Count:2b}]}] run clear @s stone_sword 1

#湿垃圾
execute as @e[type=armor_stand,tag=WTBL] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=WTBL,scores={reload=400..}] run kill @s
execute as @e[type=minecraft:arrow,tag=GGHLDY] run scoreboard players add @s reload 1
execute as @e[type=minecraft:arrow,tag=LMJSDY] run scoreboard players add @s reload 1
execute as @e[type=minecraft:arrow,tag=GGHLDY,scores={reload=60..}] run kill @s
execute as @e[type=minecraft:arrow,tag=LMJSDY,scores={reload=20..}] run kill @s
execute as @e[type=arrow,tag=LMJSDY] run data merge entity @s {pickup:0b}
execute as @e[type=arrow,tag=GGHDDY] run data merge entity @s {pickup:0b}
execute as @a[tag=SLG] if entity @s[scores={deathcount=1..}] run kill @e[type=armor_stand,tag=WTBL]
execute as @e[tag=LXMD] at @s run particle enchant ^ ^ ^ 0 2 0 0.0 3 force
execute as @e[type=armor_stand,tag=LXMD] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=LXMD,scores={reload=400..}] run kill @s
execute as @a[tag=WTBL1] run scoreboard players add @s WTBL1 1
execute as @a[tag=SLG] at @s[nbt={Inventory:[{Slot:-106b,id:"minecraft:target"}]}] run execute as @e[tag=LXMD,type=minecraft:armor_stand] at @s run tag @a[distance=0..2] add WTBL1
execute as @a if entity @s[scores={WTBL1=1,health=21..}] run tellraw @p[tag=SLG,nbt={Inventory:[{Slot:-106b,id:"minecraft:target"}]}] {"color":"yellow","text":"有客人到了"}
execute as @a if entity @s[scores={WTBL1=1,health=..20}] run tellraw @p[tag=SLG,nbt={Inventory:[{Slot:-106b,id:"minecraft:target"}]}] {"color":"yellow","text":"该下达逐客令了"}
execute as @a if entity @s[tag=SLG,scores={WTBL1=1,health=17..}] run tellraw @p[tag=SLG,nbt={Inventory:[{Slot:-106b,id:"minecraft:target"}]}] {"color":"yellow","text":"不对，怎么是我"}
execute as @a if entity @s[tag=SLG,scores={WTBL1=1,health=..16}] run tellraw @p[tag=SLG,nbt={Inventory:[{Slot:-106b,id:"minecraft:target"}]}] {"color":"red","text":"你下次得注意点"}
execute as @a if entity @s[tag=!SLG,scores={WTBL1=1,health=21..}] run tellraw @s {"color":"yellow","text":"一点薄礼"}
execute as @a if entity @s[tag=!SLG,scores={WTBL1=1,health=..20}] run tellraw @s {"color":"red","text":"此路不通"}
execute as @a if entity @s[tag=!SLG,scores={WTBL1=1}] at @s run playsound minecraft:entity.player.levelup voice @s ~ ~ ~ 1 1.9
execute as @a if entity @s[tag=!SLG,scores={WTBL1=1}] at @s run playsound minecraft:entity.player.levelup voice @p[tag=SLG] ~ ~ ~ 1 1.4
execute as @a if entity @s[tag=SLG,scores={WTBL1=1}] at @s run playsound minecraft:block.conduit.deactivate voice @s ~ ~ ~ 1 2
execute as @e[tag=WTBL1] at @s run particle enchant ~ ~ ~ 1 1 1 1 50 force
execute as @a if entity @s[scores={WTBL1=70..}] run tag @s remove WTBL1
execute as @a if entity @s[scores={WTBL1=70..}] run scoreboard players set @s WTBL1 0
execute as @a[tag=SLG] at @s[nbt={Inventory:[{Slot:-106b,id:"minecraft:target"}]}] run execute as @a at @s run kill @e[type=minecraft:armor_stand,tag=LXMD,distance=0..2]
execute as @e[type=minecraft:armor_stand,limit=1,tag=WTBL] run tp @s[tag=WTBL1]
execute as @e[type=minecraft:armor_stand,limit=1,tag=WTBL] run tp @s[tag=WTBL2]
execute as @a[tag=WTBL2] at @s run tp @s
execute as @a[tag=WTBL1] at @s run tp @s
execute as @a[tag=GGHL] at @s run scoreboard players add @s reload 1
execute as @a[tag=GGHL] at @s if entity @s[scores={reload=440}] run clear @s minecraft:crossbow
execute as @a[tag=GGHL] at @s if entity @s[scores={reload=440}] run clear @s minecraft:tipped_arrow
execute as @a[tag=GGHL] at @s if entity @s[scores={reload=440}] run tag @s remove GGHL
execute as @a[tag=SLG] at @s if entity @s[scores={reload=440}] run scoreboard players set @s reload 0
execute as @a[tag=LMJS] at @s run scoreboard players add @s LMJS 1
execute as @a[tag=LMJS] at @s run particle cherry_leaves ~ ~ ~ 11 11 11 1 5 force
execute as @a[tag=LMJS] at @s run particle dragon_breath ~ ~ ~ 1.5 3 1.5 0 1 force
execute as @a[tag=LMJS] at @s if entity @s[scores={LMJS=200}] run clear @s minecraft:crossbow
execute as @a[tag=LMJS] at @s if entity @s[scores={LMJS=200}] run clear @s minecraft:tipped_arrow
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=200}] run give @s target[custom_name='"§6孤高火力"'] 1
execute as @a[tag=LMJS] at @s if entity @s[scores={LMJS=200}] run tag @s remove LMJS
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=200}] run scoreboard players set @s LMJS 0
execute as @a[tag=LMJS,scores={Kills_S=1..}] run scoreboard players remove @s LMJS 50
execute as @a[tag=LMJS,scores={Kills_S=1..},nbt={Inventory:[{Slot:-106b,id:"minecraft:iron_sword"}]}] at @s run tp ^ ^ ^4
execute as @a[tag=LMJS,scores={Kills_S=1..}] run give @s tipped_arrow[potion_contents={potion:"minecraft:awkward",custom_effects:[{id:"minecraft:instant_damage",amplifier:0,duration:20},{id:"minecraft:slowness",amplifier:1,duration:40}]}] 1
execute as @a[tag=SLG] if entity @s[scores={LMJS=80}] run give @s tipped_arrow[potion_contents={potion:"minecraft:awkward",custom_effects:[{id:"minecraft:instant_damage",amplifier:0,duration:20},{id:"minecraft:slowness",amplifier:1,duration:40}]}] 1
execute as @a[tag=SLG] if entity @s[scores={LMJS=40}] run give @s tipped_arrow[potion_contents={potion:"minecraft:awkward",custom_effects:[{id:"minecraft:instant_damage",amplifier:0,duration:20},{id:"minecraft:slowness",amplifier:1,duration:40}]}] 1
execute as @a[tag=SLG] if entity @s[scores={LMJS=120}] run give @s tipped_arrow[potion_contents={potion:"minecraft:awkward",custom_effects:[{id:"minecraft:instant_damage",amplifier:0,duration:20},{id:"minecraft:slowness",amplifier:1,duration:40}]}] 1
execute as @a[tag=SLG] if entity @s[scores={LMJS=160}] run give @s tipped_arrow[potion_contents={potion:"minecraft:awkward",custom_effects:[{id:"minecraft:instant_damage",amplifier:0,duration:20},{id:"minecraft:slowness",amplifier:1,duration:40}]}] 1
execute as @a[tag=SLG] if entity @s[scores={LMJS=200}] run give @s tipped_arrow[potion_contents={potion:"minecraft:awkward",custom_effects:[{id:"minecraft:instant_damage",amplifier:0,duration:20},{id:"minecraft:slowness",amplifier:1,duration:40}]}] 1
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=40}] run tag @a[distance=11..15] add WTBL2
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=160}] run tag @a[distance=11..15] add WTBL2
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=80}] run tag @a[distance=11..15] add WTBL2
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=120}] run tag @a[distance=11..15] add WTBL2
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=199}] run tag @a[distance=11..15] add WTBL2
execute as @a[tag=WTBL2] run scoreboard players add @s WTBL2 1
execute as @a[tag=WTBL2] if entity @s[scores={WTBL2=3}] run tag @s remove WTBL2
execute as @a if entity @s[scores={WTBL2=3}] run effect give @s glowing infinite
execute as @a if entity @s[scores={WTBL2=3}] run scoreboard players set @s WTBL2 0
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=40}] run effect give @a[distance=11..15] glowing 1 0
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=80}] run effect give @a[distance=11..15] glowing 1 0
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=120}] run effect give @a[distance=11..15] glowing 1 0
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=160}] run effect give @a[distance=11..15] glowing 1 0
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=199}] run effect give @a[distance=11..15] glowing 1 0
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=40}] run playsound minecraft:entity.player.levelup voice @a[distance=11..15] ~ ~ ~ 100 1.9
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=80}] run playsound minecraft:entity.player.levelup voice @a[distance=11..15] ~ ~ ~ 100 1.9
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=120}] run playsound minecraft:entity.player.levelup voice @a[distance=11..15] ~ ~ ~ 100 1.9
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=160}] run playsound minecraft:entity.player.levelup voice @a[distance=11..15] ~ ~ ~ 100 1.9
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=199}] run playsound minecraft:entity.player.levelup voice @a[distance=11..15] ~ ~ ~ 100 1.9
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=40}] run execute at @a[distance=1..5] as @s run playsound minecraft:entity.warden.heartbeat voice @p[tag=SLG,distance=0..8] ~ ~ ~ 1 1.5
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=80}] run execute at @a[distance=1..5] as @s run playsound minecraft:entity.warden.heartbeat voice @p[tag=SLG,distance=0..8] ~ ~ ~ 1 1.5
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=120}] run execute at @a[distance=1..5] as @s run playsound minecraft:entity.warden.heartbeat voice @p[tag=SLG,distance=0..8] ~ ~ ~ 1 1.5
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=160}] run execute at @a[distance=1..5] as @s run playsound minecraft:entity.warden.heartbeat voice @p[tag=SLG,distance=0..8] ~ ~ ~ 1 1.5
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=199}] run execute at @a[distance=1..5] as @s run playsound minecraft:entity.warden.heartbeat voice @p[tag=SLG,distance=0..8] ~ ~ ~ 1 1.5
execute as @a[tag=SLG] at @s if entity @s[scores={LMJS=199..}] run tag @s add LMJSJS
execute as @a[tag=SLG,scores={Kills_S=1..}] run give @s cooked_beef 2
execute as @a[tag=SLG,scores={Kills_S=1..}] run clear @s arrow
execute as @a[tag=SLG,tag=LMJS] at @s run data merge entity @e[type=minecraft:arrow,tag=LMJSDY,sort=nearest,limit=1] {NoGravity:1}
execute as @e[type=arrow,tag=LMJSDY,nbt={inGround:0b}] at @s run particle sonic_boom ~ ~ ~ 0.3 0.3 0.3 0 1 force
execute as @e[type=arrow,tag=LMJSDY,nbt={inGround:0b}] at @s run particle dragon_breath ~ ~ ~ 0.3 0.3 0.3 0 5 force
execute as @e[type=arrow,tag=LMJSDY,nbt={inGround:0b}] at @s run particle lava ~ ~ ~ 0.3 0.3 0.3 0 4 force
execute as @e[type=arrow,tag=GGHLDY,nbt={inGround:0b}] at @s run particle soul ~ ~ ~ 1 1 1 0 3 force
execute as @e[type=arrow,tag=GGHLDY,nbt={inGround:1b}] at @s run kill @s
execute as @e[type=arrow,tag=LMJSDY,nbt={inGround:1b}] at @s run kill @s
execute as @a[scores={deathcount=1..}] run scoreboard players set @s LMJS 0
execute as @a[tag=SLG,scores={deathcount=1..}] run scoreboard players set @s reload 0
execute as @a[scores={deathcount=1..}] run scoreboard players set @s GGHLCS 0
execute as @a[tag=SLG,scores={deathcount=1..}] run kill @e[type=arrow,tag=LMJSDY]
execute as @a[tag=SLG,scores={deathcount=1..}] run kill @e[type=arrow,tag=GGHLDY]
execute as @a[tag=SLG,scores={Kills_S=1..}] run scoreboard players add @s WCK 1
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=!LMJS,tag=!GGHL,scores={WCK=1}] {"color":"black","text":"枪打出头鸟"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=GGHL,scores={WCK=1}] {"color":"black","text":"出局"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=LMJS,scores={WCK=1}] [{"color":"gold","text":"好戏才"},{"color":"red","text":"刚开场呢"}]
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=!GGHL,scores={WCK=2}] {"color":"gray","text":"又解决一个"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=GGHL,scores={WCK=2}] {"color":"yellow","text":"百发百中"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=LMJS,scores={WCK=2}] {"color":"red","text":"你的终点到了"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=GGHL,scores={WCK=3}] {"color":"yellow","text":"再来三个也是一样"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=LMJS,scores={WCK=3}] {"color":"gold","text":"无与伦比!"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[tag=!GGHL,tag=!LMJS,scores={WCK=3}] {"color":"yellow","text":"好事成三"}
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run title @s[tag=LMJS,scores={WCK=4}] title ["",{"text":"我挚爱的","color":"yellow"},{"text":"杰作!","color":"gold"}]
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run title @s[scores={WCK=5}] title ["",{"text":"令人","color":"yellow"},{"text":"沉醉!","color":"gold"}]
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[scores={WCK=5,health=32}] [{"color":"gold","text":"一尘不染,"},{"color":"yellow","text":"完美"}]
execute as @a[tag=SLG,scores={Kills_S=1}] at @s run tellraw @s[scores={WCK=5,health=..8}] [{"color":"gold","text":"他们干脆投降算了"}]
execute as @a[tag=!NG,scores={GGHLCS=3}] at @s run tellraw @s [{"color":"gold","text":"我觉得已经和他们耗的够久了"}]
execute as @a[tag=NG,scores={GGHLCS=4}] at @s run tellraw @s {"color":"yellow","text":"是时候和他们说再见了"}
execute as @a[scores={GGHLCS=3}] at @s run tag @s add NG
execute as @a if entity @s[tag=NG,scores={GGHLCS=2}] run scoreboard players set @s GGHLCS 0
execute as @a[scores={GGHLCS=4}] at @s run scoreboard players set @s GGHLCS 0
execute as @a[tag=SLGDC] run scoreboard players add @s DCYX 1
execute as @a[scores={DCYX=5}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 0.8
execute as @a[scores={DCYX=5}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 2
execute as @a[scores={DCYX=5}] at @s run playsound minecraft:entity.experience_orb.pickup voice @s ~ ~ ~ 100 0.6
execute as @a[scores={DCYX=9}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 0.8
execute as @a[scores={DCYX=9}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 2
execute as @a[scores={DCYX=9}] at @s run playsound minecraft:entity.experience_orb.pickup voice @s ~ ~ ~ 100 0.6
execute as @a[scores={DCYX=17}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 0.8
execute as @a[scores={DCYX=17}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 2
execute as @a[scores={DCYX=17}] at @s run playsound minecraft:entity.experience_orb.pickup voice @s ~ ~ ~ 100 0.6
execute as @a[scores={DCYX=21}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 1
execute as @a[scores={DCYX=23}] at @s run playsound minecraft:block.bell.use voice @s ~ ~ ~ 100 2
execute as @a[scores={DCYX=21}] at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 100 1.1
execute as @a[scores={DCYX=30..}] run tag @s remove SLGDC
execute as @a[scores={DCYX=30..}] run scoreboard players set @s DCYX 0
execute as @a[tag=SLG,tag=!LMJS,nbt={Inventory:[{Slot:-106b,id:"minecraft:crossbow"}]}] at @s run effect give @s speed 1 0
execute as @a[tag=SLG,tag=!LMJS] at @s if entity @s[nbt={SelectedItem:{id:"minecraft:crossbow"}}] run effect give @s speed 1 0

#狼人
execute as @a[tag=Wolf] if entity @s[tag=Wolf_S] run scoreboard players add @s reload 1
execute as @a[tag=Wolf] if entity @s[scores={reload=200..}] run effect give @s blindness 10 1
execute as @a[tag=Wolf] if entity @s[scores={reload=200..}] run effect give @s nausea 10 2
execute as @a[tag=Wolf] if entity @s[scores={reload=200..}] run effect give @s poison 10 0
execute as @a[tag=Wolf] if entity @s[scores={reload=200..}] run tag @s remove Wolf_S
execute as @a[tag=Wolf] if entity @s[scores={reload=200..}] run scoreboard players set @s reload 0

#忍者
execute as @a[tag=ninja] at @s run effect give @s minecraft:speed 1 0
execute as @a[tag=ninja] if entity @s[tag=INVIS] run scoreboard players add @s reload 1

execute as @a[tag=ninja] if entity @s[tag=INVIS] run item replace entity @s armor.head with air 1
execute as @a[tag=ninja] if entity @s[tag=INVIS] run item replace entity @s armor.chest with air 1
execute as @a[tag=ninja] if entity @s[tag=INVIS] run item replace entity @s armor.legs with air 1
execute as @a[tag=ninja] if entity @s[tag=INVIS] run item replace entity @s armor.feet with air 1

execute as @a[tag=INVIS] if entity @s[scores={S=1..}] run scoreboard players set @s reload 200
execute as @a[tag=INVIS] if entity @s[scores={S=1..}] run scoreboard players set @s S 0


execute as @a[tag=ninja] if entity @s[tag=INVIS,scores={reload=200..}] run item replace entity @s armor.head with leather_helmet[dyed_color=1908001] 1
execute as @a[tag=ninja] if entity @s[tag=INVIS,scores={reload=200..}] run item replace entity @s armor.chest with chainmail_chestplate[dyed_color=1908001] 1
execute as @a[tag=ninja] if entity @s[tag=INVIS,scores={reload=200..}] run item replace entity @s armor.legs with chainmail_leggings[dyed_color=1908001] 1
execute as @a[tag=ninja] if entity @s[tag=INVIS,scores={reload=200..}] run item replace entity @s armor.feet with leather_boots[dyed_color=1908001] 1

execute as @a[tag=ninja] if entity @s[tag=INVIS,scores={reload=200..}] run tag @s add clear
execute as @a[tag=ninja] if entity @s[tag=clear] run tag @s remove INVIS
execute as @a[tag=ninja] if entity @s[tag=clear] run scoreboard players set @s reload 0
execute as @a[tag=ninja] if entity @s[tag=clear] run tag @s remove clear

#自爆兵
execute as @a[tag=Boom] at @s run teleport @e[limit=1,sort=nearest,distance=0..2,tag=Boom_F,type=tnt] @s

#狱火机
execute as @e[type=armor_stand,tag=Lava] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=Lava,scores={reload=200..}] at @s run fill ~7 ~ ~-7 ~-7 ~1 ~7 air replace fire
execute as @e[type=armor_stand,tag=Lava,scores={reload=200..}] at @s run fill ~7 ~ ~-7 ~-7 ~1 ~7 air replace soul_fire
execute as @e[type=armor_stand,tag=Lava,scores={reload=200..}] run kill @s

#狱炎#
execute as @a[tag=Hellfire_Y] at @s run title @s actionbar {"text":"炎: ","color":"red","extra":[{"score":{"name":"@p","objective":"count1"}}]}
execute as @a[tag=Hellfire_Z] at @s run title @s actionbar {"text":"灼: ","color":"yellow","extra":[{"score":{"name":"@p","objective":"count2"}}]}

execute as @a[tag=Hellfire_Y] at @s run scoreboard players add @s count1 1   
execute as @a[tag=Hellfire] at @s[scores={count1=500..}] run playsound minecraft:entity.blaze.hurt player @s ~ ~ ~ 1 1 1
execute as @a[tag=Hellfire] at @s[scores={count1=500..}] run effect clear @s
execute as @a[tag=Hellfire] at @s[scores={count1=500..}] run effect give @s minecraft:saturation 1 8
execute as @a[tag=Hellfire_Y] at @s[scores={count1=500..}] run tag @s add Hellfire_Z 
execute as @a[tag=Hellfire] at @s[scores={count1=500..}] run tag @s remove Hellfire_Y 
execute as @a[tag=Hellfire] at @s[scores={count1=500..}] run effect give @s minecraft:instant_health 1 1
execute as @a[tag=Hellfire] at @s run scoreboard players set @s[scores={count1=500..}] count1 0 
execute as @a[tag=Hellfire_Z] at @s run scoreboard players add @s count2 1 
execute as @a[tag=Hellfire] at @s[scores={count2=500..}] run playsound minecraft:entity.blaze.shoot voice @s ~ ~ ~ 1 0.4 1
execute as @a[tag=Hellfire] at @s[scores={count2=500..}] run effect give @s minecraft:absorption 4 2
execute as @a[tag=Hellfire] at @s[scores={count2=500..}] run effect give @s minecraft:saturation 1 8
execute as @a[tag=Hellfire] at @s[scores={count2=500..}] run execute as @a[distance=0..4,tag=!Hellfire] run damage @s 10 minecraft:lava by @p[tag=Hellfire]
execute as @a[tag=Hellfire] at @s[scores={count2=500..}] run effect give @a[distance=0..4,tag=!Hellifre] glowing 4 0


execute as @a[tag=Hellfire_Z] at @s[scores={count2=500..}] run tag @s add Hellfire_Y  
execute as @a[tag=Hellfire] at @s[scores={count2=500..}] run tag @s remove Hellfire_Z 
execute as @a[tag=Hellfire] at @s run scoreboard players set @s[scores={count2=500..}] count2 0 

execute as @a[tag=Hellfire] at @s run scoreboard players add @s reload 1
execute as @a[tag=Hellfire,scores={reload=640}] run clear @s minecraft:blaze_powder 3
execute as @a[tag=Hellfire,scores={reload=640}] run give @s minecraft:blaze_powder[custom_name='§c不灭之火',minecraft:unbreakable={},minecraft:base_color=red,lore=['冷却时间为4s，充能时间为32s'] ] 4
execute as @a[tag=Hellfire,scores={reload=640..}] run scoreboard players set @s reload 0




execute as @a[tag=Hellfire_Y] at @s run effect give @s minecraft:fire_resistance 1 0
execute as @a[tag=Hellfire_Y] at @s run effect give @s minecraft:glowing 1 0
execute as @a[tag=Hellfire_Y] at @s if block ~ ~-1 ~ minecraft:lava run effect give @s minecraft:jump_boost 2 1 true
execute as @a[tag=Hellfire_Z] at @s run effect give @s minecraft:absorption 1 0
execute as @a[tag=Hellfire_Z] at @s run effect give @s minecraft:speed 2 0
execute as @a[tag=Hellfire_Z] at @s run effect clear @s minecraft:slowness   
execute as @a[tag=Hellfire_Z] at @s run effect give @s minecraft:weakness 1 4
execute as @a[tag=Hellfire_INS] at @s run execute as @a[distance=0..1.5,tag=!Hellfire] run damage @s 1 minecraft:lava by @p[tag=Hellfire_INS]
execute as @a[tag=Hellfire_INS] at @s run scoreboard players add @s count3 1 
execute as @a[tag=Hellfire_INS,scores={count3=40}] at @s run tag @s remove Hellfire_INS
execute as @a[tag=Hellfire,scores={count3=40}] at @s run scoreboard players set @s count3 0



execute as @e[type=armor_stand,tag=Hellfire] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=Hellfire,scores={reload=180..}] at @s run fill ~7 ~-1 ~-7 ~-7 ~1 ~7 air replace fire
execute as @e[type=armor_stand,tag=Hellfire,scores={reload=180..}] at @s run fill ~7 ~-1 ~-7 ~-7 ~1 ~7 air replace soul_fire
execute as @e[type=armor_stand,tag=Hellfire] at @s run effect give @a[tag=!Hellfire,distance=0..7] minecraft:slowness 2 6
execute as @e[type=armor_stand,tag=Hellfire] at @s run effect give @a[tag=!Hellfire,distance=0..7] minecraft:weakness 2 0
execute as @e[type=armor_stand,tag=Hellfire] at @s run effect clear @a[tag=!Hellfire,distance=0..7] minecraft:absorption
execute as @e[type=armor_stand,tag=Hellfire] at @s run effect clear @a[tag=!Hellfire,distance=0..7] minecraft:regeneration
execute as @e[type=armor_stand,tag=Hellfire] at @s run kill @e[type=arrow,distance=0..7]
execute as @e[type=armor_stand,tag=Hellfire] at @s run kill @e[type=minecraft:tnt,distance=0..7]
execute as @e[type=armor_stand,tag=Hellfire] at @s run kill @e[type=minecraft:tnt_minecart,distance=0..7]
execute as @e[type=armor_stand,tag=Hellfire] at @s run kill @e[type=minecraft:creeper,distance=0..7]
execute as @e[type=armor_stand,tag=Hellfire] at @s run particle minecraft:flame ~ ~ ~ 6 6 6 0 20
execute as @e[type=armor_stand,tag=Hellfire] at @s run particle minecraft:lava ~ ~ ~ 6 6 6 0 13
execute as @e[type=armor_stand,tag=Hellfire,scores={reload=180..}] at @s run kill @s

execute as @a[tag=Hellfire_Y] at @s run particle minecraft:lava ~ ~-0.6 ~ 0 0 0 0 1
execute as @a[tag=Hellfire_Z] at @s run particle minecraft:flame ~ ~0.35 ~ 0 0 0 0 1



execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s count1 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s count2 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s count3 0






#迪奥
execute as @e[tag=DIO,scores={Kills_S=1..}] run give @s minecraft:arrow 6





#无常
execute as @a[tag=wuchang] run item replace entity @s container.0 with fishing_rod[custom_name='"§8索命"',lore=['"§m你不是第一个，也不会最后一个。"'],enchantments={levels:{"minecraft:bane_of_arthropods":10,"minecraft:smite":10}},attribute_modifiers=[{id:"armor",type:"generic.attack_damage",amount:9.5d,operation:"add_value",slot:"mainhand"},{id:"armor",type:"generic.knockback_resistance",amount:0.4d,operation:"add_value",slot:"mainhand"},{id:"armor",type:"generic.armor",amount:2.0d,operation:"add_value",slot:"offhand"},{id:"armor",type:"generic.armor_toughness",amount:2.0d,operation:"add_value",slot:"offhand"},{id:"armor",type:"generic.movement_speed",amount:0.05d,operation:"add_multiplied_base",slot:"offhand"}]] 1
execute as @a[tag=wuchang] run item replace entity @s armor.head with compass[enchantments={levels:{"minecraft:blast_protection":5,"minecraft:binding_curse":10}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:1.0d,operation:"add_value",slot:"mainhand"},{id:"armor",type:"generic.armor_toughness",amount:1.0d,operation:"add_value",slot:"mainhand"},{id:"armor",type:"generic.knockback_resistance",amount:0.1d,operation:"add_value",slot:"mainhand"}]] 1
execute as @a[tag=wuchang] run item replace entity @s armor.legs with leather_leggings[minecraft:unbreakable={1:1},enchantments={levels:{"minecraft:blast_protection":9}},attribute_modifiers=[{id:"armor",type:"generic.armor_toughness",amount:4.0d,operation:"add_value",slot:"legs"}],dyed_color=0] 1
execute as @a[tag=wuchang] run item replace entity @s armor.chest with leather_chestplate[minecraft:unbreakable={1:1},enchantments={levels:{"minecraft:fire_protection":5}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:7.0d,operation:"add_value",slot:"chest"},{id:"armor",type:"generic.armor_toughness",amount:1.0d,operation:"add_value",slot:"chest"}],dyed_color=0] 1
execute as @a[tag=wuchang] run item replace entity @s armor.feet with leather_boots[minecraft:unbreakable={1:1},enchantments={levels:{"minecraft:feather_falling":10}},attribute_modifiers=[{id:"armor",type:"generic.movement_speed",amount:0.2d,operation:"add_multiplied_base",slot:"feet"},{id:"armor",type:"generic.armor",amount:2.0d,operation:"add_value",slot:"feet"}],dyed_color=0] 1
execute as @a[tag=wuchang] run tag @s remove wuchang
execute as @a[tag=WC1] run scoreboard players add @s reload 1
execute as @a[tag=WC1] if entity @s[scores={reload=2}] run damage @s 19
execute as @a[tag=WC1] if entity @s[scores={reload=3}] run tag @s remove WC1
execute as @a[tag=NoChang] if entity @s[scores={reload=3}] run scoreboard players set @s reload 0
execute as @a[tag=WC2,scores={Kills_S=1..}] run effect give @a[scores={health=..20}] glowing 5 0
execute as @a[tag=WC2,scores={Kills_S=1..}] run effect give @s instant_health 1 20
execute as @a[tag=WC2,scores={Kills_S=1..}] run effect give @s strength 4 0
execute as @a[tag=WC2] run scoreboard players add @s WC2 1
execute as @a[tag=WC2] if entity @s[scores={WC2=101}] run tag @s remove WC2
execute as @a[tag=NoChang] if entity @s[scores={WC2=101}] run scoreboard players set @s WC2 0
execute as @a[tag=ZS] run scoreboard players add @s ZS 1
execute as @a[tag=ZS] if entity @s[scores={ZS=102}] run tag @s remove ZS
execute as @a if entity @s[scores={ZS=102}] run clear @s minecraft:totem_of_undying
execute as @a if entity @s[scores={ZS=102}] run scoreboard players set @s ZS 0
execute as @a[tag=ZS] if entity @s[nbt=!{SelectedItem:{id:"minecraft:totem_of_undying"}},scores={ZS=80}] run damage @s 25 out_of_world by @p[tag=NoChang]
execute as @a[tag=ZS] if entity @s[nbt=!{SelectedItem:{id:"minecraft:totem_of_undying"}},scores={ZS=100}] run damage @s 25 out_of_world by @p[tag=NoChang]
execute as @a[tag=ZS] if entity @s[scores={ZS=20}] run tellraw @s {"color":"green","text":"5"}
execute as @a[tag=ZS] if entity @s[scores={ZS=40}] run tellraw @s {"color":"green","text":"4"}
execute as @a[tag=ZS] if entity @s[scores={ZS=60}] run tellraw @s {"color":"green","text":"3"}
execute as @a[tag=ZS] if entity @s[scores={ZS=80}] run tellraw @s {"color":"red","text":"2"}
execute as @a[tag=ZS] if entity @s[scores={ZS=99}] run tellraw @s {"color":"red","text":"1"}
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=10}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=30}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=50}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=60}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=70}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=80}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a[tag=ZS] at @s if entity @s[scores={ZS=90}] run playsound minecraft:entity.warden.heartbeat voice @s ~ ~ ~ 1 2
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s ZS 0
execute as @a[tag=NoChang,scores={Kills_S=1..}] run effect give @s instant_health 1 1
execute as @a[tag=NoChang,scores={Kills_S=1..}] run scoreboard players add @s WCK 1
execute as @a[tag=NoChang,scores={Kills_S=1..}] run effect give @s resistance 2 4
execute as @a[tag=NoChang,scores={Kills_S=1..}] at @s run tellraw @s {"color":"black","text":"又多了一份灵魂"}
execute as @a[tag=NoChang,scores={Kills_S=1..}] at @s run tellraw @s[scores={WCK=2}] {"color":"black","text":"两份灵魂了"}
execute as @a[tag=NoChang,scores={Kills_S=1..}] at @s run tellraw @s[scores={WCK=2}] {"color":"gray","text":"今天收获不错"}
execute as @a[tag=NoChang,scores={Kills_S=1..}] at @s run tellraw @s[scores={WCK=3}] {"color":"black","text":"三份灵魂"}
execute as @a[tag=NoChang,scores={Kills_S=1..}] at @s run tellraw @s[scores={WCK=3}] {"color":"red","text":"我现在很愉悦！"}
execute as @a[tag=NoChang,scores={WCK=1..,health=..15}] run effect give @s fire_resistance 2 0
execute as @a[tag=NoChang,scores={WCK=1..,health=..15}] run effect give @s water_breathing 2 0
execute as @a[tag=NoChang,scores={WCK=1..,health=..15}] run effect give @s jump_boost 2 0
execute as @a[tag=NoChang,scores={WCK=2..,health=..15}] run effect give @s speed 2 0
execute as @a[tag=NoChang,scores={WCK=2..,health=..15}] run effect give @s slowness infinite
execute as @a[tag=NoChang,scores={WCK=2..,health=..15}] run effect give @s wither infinite
execute as @a[tag=NoChang,scores={WCK=3..,health=..15}] run effect give @s strength 2 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players add @a[distance=0..5,tag=NoChang] WCK 1
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s WCK 0






#时间管理者
execute as @a[tag=TWorld] run scoreboard players add @s reload 1
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run effect give @s blindness 1 0
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run effect give @s night_vision 1 0
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run effect give @s slowness 1 254
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run effect give @s jump_boost 1 254
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run effect give @s weakness 2 254
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run title @s title "§c§lThe World"
execute as @a[tag=!TWorld] at @s if entity @a[tag=TWorld_S,distance=0..8] run title @s subtitle "§c§l时间被停止了......"
execute as @a[tag=TWorld] run tag @s remove TWorld_S
execute as @a[tag=!TWorld] at @s run teleport @s @e[limit=1,sort=nearest,distance=0..1,tag=TWorld_A,type=armor_stand]
execute as @a[tag=TWorld,scores={reload=100..}] run tag @s add TWorld_T
execute as @a[tag=TWorld,scores={reload=100..}] run tag @s remove TWorld
execute as @a[tag=TWorld_T,scores={reload=100..}] run effect give @s weakness 1 8
execute as @a[tag=TWorld_T,scores={reload=100..}] run effect give @s speed 1 1
execute as @a[tag=TWorld_T,scores={reload=100..}] run scoreboard players set @s reload 0
execute as @a[tag=TWorld_T] run tag @s remove TWorld_T

#溺尸
execute as @a[tag=Drowned] at @s if block ~ ~-0.1 ~ minecraft:water run effect give @s minecraft:regeneration 2 0
execute if entity @a[tag=Drowned]
execute as @a[tag=!Drowned_S] at @s if entity @e[tag=Drowned_A,distance=0..8] run tag @s add Drowned_Marked
execute as @a[tag=Drowned_S] at @s if entity @s[tag=Drowned_S] run scoreboard players add @s reload 1
execute as @a[tag=Drowned] at @s if entity @s[scores={reload=200..}] run tag @s remove Drowned_S
execute as @a[tag=Drowned] at @s if entity @s[scores={reload=200..}] run scoreboard players set @s reload 0
execute as @e[tag=Drowned_A] at @s if entity @s[tag=Drowned_A] run particle glow ~ ~ ~ 3 3 3 0 48 force
execute as @e[tag=Drowned_A] at @s if entity @s[tag=Drowned_A] run scoreboard players add @s passiveReload 1
execute as @e[tag=Drowned_A] at @s if entity @s[scores={passiveReload=200..}] run kill @s
execute as @a[tag=Drowned_Marked] at @s unless entity @e[tag=Drowned_A,distance=0..8] run scoreboard players set @s passiveReload 0
execute as @a[tag=Drowned_Marked] at @s unless entity @e[tag=Drowned_A,distance=0..8] run tag @s remove Drowned_Marked
execute as @a[tag=Drowned_Marked] if entity @s[tag=Drowned_Marked] run scoreboard players add @s passiveReload 1
execute as @a[tag=Drowned_Marked] at @s if entity @s[scores={passiveReload=20..}] run summon lightning_bolt ~ ~ ~
execute as @a[tag=Drowned_Marked] if entity @s[scores={passiveReload=20..}] run scoreboard players set @s passiveReload 0
execute as @a[tag=Drowned_S] if entity @s[scores={deathcount=1}] run scoreboard players set @s passiveReload 0

#冰人
execute as @e[tag=IceMan_Arrow,nbt={inGround:0b}] at @s run particle snowflake ~ ~0 ~ 0 0 0 0 3 force

#快刀手
execute as @a[tag=FastSword_S] run scoreboard players add @s reload 1
execute as @a[tag=FastSword_S] if entity @s[scores={reload=60..}] run tag @s add FastSword_Short
execute as @a[tag=FastSword_Short] run tag @s remove FastSword_S
execute as @a[tag=FastSword_Short] run scoreboard players set @s reload 0
execute as @a[tag=FastSword_Short] run tag @s remove FastSword_Short

#夜空新职业#
execute as @a[tag=Changer] if entity @s[scores={reload=180..}] run scoreboard players set @s reload 180
execute as @a[tag=gun2,tag=gun,tag=!sword,tag=!dante] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=sword1,tag=sword,tag=!gun,tag=!dante] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=sword2,tag=sword,tag=!gun,tag=!dante] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=sword3,tag=sword,tag=!gun,tag=!dante] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=gun,tag=gun1] if entity @s[nbt={Inventory:[{id:"minecraft:tipped_arrow",Count:3b}]}] run clear @s minecraft:tipped_arrow 1
execute as @a[tag=gun,tag=gun1] if entity @s[nbt={Inventory:[{id:"minecraft:tipped_arrow",Count:2b}]}] run clear @s minecraft:tipped_arrow 1

#火焰豌豆#
execute as @e[type=arrow,tag=FirePea,nbt={inGround:0b}] at @s run particle lava ~ ~ ~ 0 0 0 0 3 force
execute as @e[type=arrow,tag=FirePea,nbt={inGround:1b}] at @s run summon creeper ~ ~ ~ {ExplosionRadius:2b,Fuse:0,Tags:["FirePea_TNT"]}
execute as @e[type=arrow,tag=FirePea,nbt={inGround:1b}] at @s run kill @s
execute as @e[type=arrow,tag=FirePea_Fire,nbt={inGround:0b}] at @s run particle flame ~ ~ ~ 0 0 0 0 1 normal
execute as @a[tag=FireMan_S] run scoreboard players add @s reload 1
execute as @a[tag=FireMan] if entity @s[scores={reload=100..}] run tag @s remove FireMan_S
execute as @a[tag=FireMan] if entity @s[scores={reload=100..}] run scoreboard players set @s reload 0

#黑暗法则#
execute as @a[tag=darklaw1] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=darklaw] if entity @s[scores={reload=101..}] run item replace entity @s armor.chest with golden_chestplate[enchantments={levels:{"minecraft:blast_protection":1}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:4.0d,operation:"add_value",slot:"chest"},{id:"armor",type:"generic.knockback_resistance",amount:-0.1d,operation:"add_multiplied_base",slot:"chest"}]] 1
execute as @a[tag=darklaw] if entity @s[scores={reload=101..}] run item replace entity @s armor.head with iron_helmet[enchantments={levels:{"minecraft:protection":1}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:3.0d,operation:"add_value",slot:"head"}]] 1
execute as @a[tag=darklaw] if entity @s[scores={reload=101..}] run item replace entity @s armor.legs with chainmail_leggings[enchantments={levels:{"minecraft:fire_protection":1}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:3.0d,operation:"add_value",slot:"legs"}]] 1
execute as @a[tag=darklaw1] if entity @s[scores={reload=101..}] run tag @s remove darklaw1
execute as @a[tag=darklaw] if entity @s[scores={reload=101..}] run scoreboard players set @s reload 0
execute if entity @a[tag=darklaw,scores={health=20..}]
execute as @a[tag=darklaw] if entity @s[scores={health=40..}] run effect give @s resistance 2 0
execute as @a[tag=darklaw] if entity @s[scores={health=40..46}] run effect give @s weakness 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=29..46}] run effect give @s night_vision 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=29..39}] run effect give @s speed 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=29..39}] run effect give @s jump_boost 1 1
execute as @a[tag=darklaw] if entity @s[scores={health=17..28}] run effect give @s invisibility 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=17..28}] run effect give @s strength 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=..30}] run effect give @s conduit_power 2 0
execute as @a[tag=darklaw] if entity @s[scores={health=..10}] run effect give @s fire_resistance 2 0
execute as @a[tag=darklaw] if entity @s[scores={health=9..16}] run effect give @s slow_falling 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=9..16}] run effect give @s mining_fatigue 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=9..16}] run effect give @s strength 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=0..8}] run effect give @s regeneration 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=0..8}] run effect give @s resistance 1 0
execute as @a[tag=darklaw] if entity @s[scores={health=..10}] run effect give @s glowing 2 0

#铐#
execute as @a[tag=SC1] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=SC1] if entity @s[scores={reload=111..}] run item replace entity @s armor.head with leather_helmet[enchantments={levels:{"minecraft:protection":1}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:4.0d,operation:"add_value",slot:"head"}],dyed_color=0] 1
execute as @a[tag=SC1] if entity @s[scores={reload=111..}] run item replace entity @s armor.legs with chainmail_leggings[enchantments={levels:{"minecraft:protection":1}},attribute_modifiers=[{id:"armor",type:"generic.armor",amount:2.0d,operation:"add_value",slot:"legs"}]] 1
execute as @a[tag=SC1] if entity @s[scores={reload=111..}] run item replace entity @s armor.feet with leather_boots[attribute_modifiers=[{id:"armor",type:"generic.movement_speed",amount:0.4d,operation:"add_multiplied_base",slot:"feet"},{id:"armor",type:"generic.armor",amount:2.0d,operation:"add_value",slot:"feet"}],dyed_color=0] 1
execute as @a[tag=SC1] if entity @s[scores={reload=111..}] run item replace entity @s armor.chest with iron_chestplate[attribute_modifiers=[{id:"armor",type:"generic.armor",amount:3.0d,operation:"add_value",slot:"chest"},{id:"armor",type:"generic.movement_speed",amount:0.1d,operation:"add_multiplied_base",slot:"chest"}]] 1
execute as @a[tag=SC1] if entity @s[scores={reload=111..}] run tag @s remove SC1
execute as @a[tag=Scout] if entity @s[scores={reload=111..}] run tag @a[distance=1..7,tag=!LW] add LW
execute as @a[tag=Scout] if entity @s[scores={reload=111..}] run scoreboard players set @s reload 0
execute as @e[type=minecraft:armor_stand,tag=soul] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=soul,scores={reload=300..}] run kill @s
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run particle soul ~ ~1 ~ 2.0 2.0 2.0 0 35 force
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run particle dripping_water ~ ~1 ~ 2.0 2.0 2.0 0 20 force
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run particle dragon_breath ~ ~1 ~ 1.0 1.0 1.0 0 20 force
execute as @e[type=minecraft:snowball] at @e[type=minecraft:snowball] run teleport @e[tag=soul,type=minecraft:armor_stand] @s
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run effect give @a[distance=0..3,tag=!Scout,tag=!LW] wither 1 5
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run effect give @a[distance=0..3,tag=!Scout,tag=!LW] weakness 1 1
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run damage @a[limit=1,distance=0..3,tag=LW] 4 player_attack by @p[tag=Scout]
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run effect give @a[distance=0..3,tag=!Scout,tag=LW] glowing 2 1
execute as @e[type=minecraft:armor_stand,tag=soul] at @s run effect give @a[distance=0..3,tag=!Scout,tag=LW] slowness 2 2
execute as @e[type=minecraft:armor_stand,tag=Jsoul] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=Jsoul,scores={reload=300..}] run kill @s
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run particle cloud ~ ~1 ~ 3.0 3.0 3.0 0 15 force
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run particle firework ~ ~1 ~ 3.0 3.0 3.0 0 20 force
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run particle lava ~ ~1 ~ 3.0 3.0 3.0 0 5 force
execute as @e[type=minecraft:snowball] at @e[type=minecraft:snowball] run teleport @e[tag=Jsoul,type=minecraft:armor_stand] @s
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run effect give @a[distance=0..3,tag=!LW] speed 2 1
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run effect give @a[distance=0..3,tag=!LW] resistance 2 0
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run effect give @a[distance=0..3,tag=LW] weakness 1 1
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run effect give @a[distance=0..3,tag=LW] levitation 1 0
execute as @e[type=minecraft:armor_stand,tag=Jsoul] at @s run effect give @a[distance=0..3,tag=LW] poison 5 0
execute as @a[tag=LW] at @s run scoreboard players add @s LW 1
execute as @a[tag=LW] if entity @s[scores={LW=2400..}] run tag @s remove LW
execute as @a if entity @s[scores={LW=2400..}] run scoreboard players set @s LW 0
execute as @a[tag=LW] at @s run particle warped_spore ~ ~1 ~ 1.0 1.0 1.0 0 1 force






#渔夫
execute as @e if entity @s[tag=DyingFish] run scoreboard players add @s reload 1
execute as @e[tag=DyingFish] if entity @s[scores={reload=100..}] run kill @s

#土豆兵
execute as @e[type=slime,tag=Slime_Mine] run scoreboard players add @s reload 1
execute as @e[type=slime,tag=Slime_Mine] if entity @s[scores={reload=800..}] run kill @s
execute as @e[type=slime,tag=Slime_Mine] at @s if entity @e[type=!slime,tag=!potato,distance=0..1.5] run effect give @e[distance=0..1.5] resistance 1 1
execute as @e[type=slime,tag=Slime_Mine] at @s if entity @e[type=!slime,tag=!potato,distance=0..1.5] run effect give @e[distance=0..1.5] slowness 3 5
execute as @e[type=slime,tag=Slime_Mine] at @s if entity @e[type=!slime,tag=!potato,distance=0..1.5] run effect give @e[distance=0..1.5] weakness 1 0
execute as @e[type=slime,tag=Slime_Mine] at @s if block ~ ~0.29 ~ minecraft:air run teleport @s ~ ~-0.05 ~
execute as @e[type=slime,tag=Slime_Mine] at @s if entity @e[type=!slime,tag=!potato,distance=0..1.5] run particle lava ~ ~0.7 ~ 0 0 0 1 10 force
execute as @e[type=slime,tag=Slime_Mine] at @s if entity @e[type=!slime,tag=!potato,distance=0..1.5] run summon creeper ~ ~0.7 ~ {ExplosionRadius:2b,Fuse:0}
execute as @e[type=slime,tag=Slime_Mine] at @s run particle dust{color:[1.000,0.000,0.000],scale:1} ~ ~0.7 ~ 0 0 0 0 1 force
execute as @e[type=slime,tag=Slime_Mine] at @s if entity @e[type=!slime,tag=!potato,distance=0..1.5] run kill @s
execute as @a[tag=potato] at @s if entity @e[tag=Slime_Mine,distance=0..8] run effect give @s resistance 2 0 true
execute as @a[tag=potato] at @s run effect give @s minecraft:speed infinite 0

#火龙
execute as @a[tag=FireDragon] if entity @s[tag=FireDragon_Firing] run scoreboard players add @s reload 1
execute as @a[tag=FireDragon] if entity @s[scores={reload=60..}] run tag @s remove FireDragon_Firing
execute as @a[tag=FireDragon] at @s if entity @s[scores={reload=60..}] run playsound minecraft:entity.ender_dragon.hurt player @a ~ ~ ~ 1 1
execute as @a[tag=FireDragon] at @s if entity @s[scores={reload=60..}] run effect give @s slowness 2 0
execute as @a[tag=FireDragon] if entity @s[scores={reload=60..}] run scoreboard players set @s reload 0
execute as @e[type=armor_stand,tag=Firing] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=Firing] if entity @s[scores={reload=60..}] run kill @s
execute as @a[tag=!FireDragon_Firing] at @s if entity @e[type=minecraft:armor_stand,distance=0..5,tag=Firing] run damage @s 1.5 on_fire by @e[limit=1,distance=0..5,tag=Firing,type=minecraft:armor_stand]
execute as @a[tag=FireDragon_Firing] at @s positioned ^ ^ ^3 positioned ~ ~1 ~ run teleport @e[limit=1,distance=0..4,tag=Firing,type=minecraft:armor_stand] ~ ~ ~

#漆影
execute as @a[tag=Phantom] if entity @s[tag=Pre_Maid_Skill] run scoreboard players add @s reload 1
execute as @a[tag=Phantom] at @s if entity @s[tag=Pre_Maid_Skill,scores={reload=20..}] run playsound minecraft:entity.bat.loop player @s ~ ~ ~ 1 1
execute as @a[tag=Phantom] at @s if entity @s[tag=Pre_Maid_Skill,scores={reload=20..}] run particle smoke ~ ~1 ~ 0.5 0.5 0.5 0.1 10 force
execute as @a[tag=Phantom] at @s if entity @s[tag=Pre_Maid_Skill,scores={reload=20..}] run effect give @s slowness 1 3
execute as @a[tag=Phantom] if entity @s[tag=Pre_Maid_Skill,scores={reload=20..}] run tag @s add Maid_Skill
execute as @a[tag=Phantom] if entity @s[tag=Pre_Maid_Skill,scores={reload=20..}] run tag @s add Reset
execute as @a[tag=Phantom] if entity @s[tag=Pre_Maid_Skill,scores={reload=20..}] run tag @s remove Pre_Maid_Skill
execute as @a[tag=Phantom] if entity @s[tag=Reset,scores={reload=20..}] run scoreboard players set @s reload 0
execute as @a[tag=Phantom] if entity @s[tag=Reset] run tag @s remove Reset
execute as @a[tag=Phantom] if entity @s[tag=Maid_Skill] run scoreboard players add @s reload 1
execute as @a[tag=Phantom] if entity @s[scores={reload=40..}] run tag @s remove Maid_Skill
execute as @a[tag=Phantom] at @s if entity @s[scores={reload=40..}] run particle end_rod ~ ~1 ~ 0.5 0.5 0.5 0.1 10 force
execute as @a[tag=Phantom] at @s if entity @s[scores={reload=40..}] run playsound minecraft:entity.vindicator.death player @a
execute as @a[tag=Phantom] if entity @s[scores={reload=40..}] run scoreboard players set @s reload 0
execute as @a[tag=Phantom] at @s if entity @a[distance=1..6,scores={health=..10}] run effect give @s speed 1 1

#维克多
execute as @a[tag=PreSwap] at @s run scoreboard players add @s reload 1
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run tag @s remove PreSwap
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run summon marker ~ ~ ~ {Tags:["SwapMarker"]}
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run tag @a[distance=1..8,sort=nearest,limit=1] add Swaped
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run teleport @s @a[limit=1,sort=nearest,distance=1..10]
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run playsound minecraft:entity.illusioner.mirror_move player @a ~ ~ ~ 3 1
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run scoreboard players set @s reload 0
execute as @a[tag=Vector] at @s if entity @s[scores={reload=20..}] run effect give @s speed 3 1
execute as @e[tag=Swaped] at @s run teleport @s @e[limit=1,sort=nearest,tag=SwapMarker]
execute as @e[tag=Swaped] at @s run playsound minecraft:entity.illusioner.mirror_move player @a ~ ~ ~ 3 1
execute as @e[tag=Swaped] at @s run effect give @s darkness 4 2
execute as @e[tag=Swaped] at @s run effect give @s slowness 2 2
execute as @e[tag=Swaped] at @s run kill @e[tag=SwapMarker]
execute as @e[tag=Swaped] at @s run tag @s remove Swaped

#动力小子
execute as @a[tag=Octane] at @s if entity @s[tag=!Octane_Running,scores={health=..19,delay=..0}] run effect give @s regeneration 2 1
execute as @a[tag=Octane] at @s if entity @s[tag=!Octane_Running,scores={health=..19,delay=..0}] run scoreboard players set @s delay 39
execute as @a[tag=Octane] at @s if entity @s[tag=!Octane_Running,scores={delay=0..}] run scoreboard players remove @s delay 1
execute as @a[tag=Octane_Running] at @s run particle totem_of_undying ~ ~1 ~ 0 0.3 0 0 5 force
execute as @a[tag=Octane_Running] at @s run scoreboard players add @s reload 1
execute as @a[tag=Octane] at @s if entity @s[scores={reload=120..}] run tag @s remove Octane_Running
execute as @a[tag=Octane] at @s if entity @s[scores={reload=120..}] run playsound minecraft:entity.bat.loop player @a ~ ~ ~ 1 1.6
execute as @a[tag=Octane] at @s if entity @s[scores={reload=120..}] run scoreboard players set @s reload 0

execute as @e[type=slime,tag=JumpPad,nbt={NoAI:1b}] at @s if block ~ ~0.59 ~ minecraft:air run teleport @s ~ ~-0.05 ~
execute as @e[type=slime,tag=JumpPad] at @s unless block ~ ~-0.01 ~ minecraft:air run data merge entity @e[type=slime,limit=1,sort=nearest] {NoAI:1b}
execute as @e[type=slime,tag=JumpPad,nbt={NoAI:1b}] run scoreboard players add @s reload 1
execute as @e[type=slime,tag=JumpPad,nbt={NoAI:1b}] if entity @s[scores={reload=1000..}] run kill @s

#道士#
execute as @a[tag=Taoist_attack_regeneration] run scoreboard players add @s reload 1
execute as @a[tag=Taoist_Wolf] run scoreboard players add @s reload 1
execute as @a[tag=Taoist] at @s if entity @s[scores={reload=60..}] run tag @s remove Taoist_attack_regeneration
execute as @a[tag=Taoist,tag=!Taoist_Wolf] at @s if entity @s[scores={reload=60..}] run scoreboard players set @s reload 0
execute as @a[tag=Taoist] if entity @s[scores={reload=100}] run effect give @s minecraft:nausea 15 1
execute as @a[tag=Taoist] if entity @s[scores={reload=200..}] run effect give @s minecraft:wither 10 3
execute as @a[tag=Taoist] if entity @s[scores={reload=200..}] run effect give @s minecraft:darkness 10 3
execute as @a[tag=Taoist] if entity @s[scores={reload=200..}] run tag @s remove Taoist_Wolf
execute as @a[tag=Taoist] if entity @s[scores={reload=200..}] run scoreboard players set @s reload 0

#甘雨
execute as @a[tag=GanYu_Charging] run scoreboard players add @s reload 1
execute as @a[tag=GanYu_Charging] at @s if entity @s[scores={reload=10}] run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 5 1.4
execute as @a[tag=GanYu_Charging] at @s if entity @s[scores={reload=20}] run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 5 1.7
execute as @a[tag=GanYu_Charging] at @s if entity @s[scores={reload=30}] run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 5 2
execute as @e[tag=Ice_LianHua] at @s run particle snowflake ~ ~ ~ 0 0 0 0.3 2 force
execute as @e[tag=Ice_Big] at @s run scoreboard players add @s reload 1
execute as @e[tag=Snow_Arrow,nbt={inGround:1b}] run kill @s
execute as @e[tag=Snow_Arrow,nbt={inGround:0b}] at @s run particle snowflake ~ ~ ~ 0 0 0 0 1 force

#蟒蛇
execute as @a[tag=Poison_Resist] run effect give @s poison infinite
execute as @a[tag=Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s speed 5 0
execute as @a[tag=Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s resistance 5 0
execute as @a[tag=Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s jump_boost 5 0
execute as @e[tag=Snake_DuSu] at @s run particle totem_of_undying ~ ~1 ~ 6 0.5 6 0 20 force
execute as @e[tag=Snake_DuSu] at @s run scoreboard players add @s reload 1
execute as @e[tag=Snake_DuSu] at @s if entity @s[scores={reload=500..}] run kill @s
execute as @e[tag=!Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s poison 5 0
execute as @e[tag=!Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s slowness 5 1
execute as @e[tag=!Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s weakness 5 0
execute as @e[tag=!Snake] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s glowing 5 0
execute as @e[tag=!Snake,scores={health=..20}] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s darkness 5 2
execute as @e[tag=!Snake,scores={health=20..}] at @s if entity @e[type=minecraft:armor_stand,tag=Snake_DuSu,distance=1..16] run effect give @s blindness 5 0
execute as @e[tag=Snake_DuSu] at @s unless entity @a[tag=Snake,distance=0..16] run kill @s

#影魔
execute as @a[tag=shadowF2] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=shadowF] at @s if entity @s[scores={reload=110}] run particle entity_effect{color:[1.000,1.000,1.000,1.00]} ~ ~1 ~ 0 0 0 0 700 force
execute as @a[tag=shadowF,tag=!shadowF2] if entity @s[scores={reload=121..}] run scoreboard players set @s reload 0
execute as @a[tag=shadowF2] if entity @s[scores={reload=120..}] run gamemode adventure @s
execute as @a[tag=shadowF2] if entity @s[scores={reload=121..}] run tag @s remove shadowF2
execute as @a[tag=shadowF2] if entity @s[scores={reload=110}] run execute as @s at @s run execute as @p[distance=1..10] at @p[distance=1..10] run summon minecraft:armor_stand ~ ~ ~ {NoGravity:1b,NoBasePlate:1b,DisabledSlots:31,Invisible:1b,Tags:["shadowF1"],ArmorItems:[{id:"minecraft:leather_boots",Count:1b},{},{id:"minecraft:leather_chestplate",Count:1b},{id:"minecraft:jack_o_lantern",Count:1b}],Pose:{Head:[30f,0f,0f]}}
execute as @a[tag=shadowF2] if entity @s[scores={reload=110}] run execute as @s at @s run execute as @p[distance=1..10] at @p[distance=1..10] run item replace entity @e[distance=0..10,tag=shadowF1] armor.feet with leather_boots[enchantments={levels:{"minecraft:feather_falling":3,"minecraft:depth_strider":3,"minecraft:soul_speed":3}},attribute_modifiers=[{id:"armor",type:"generic.movement_speed",amount:0.08d,operation:"add_multiplied_base",slot:"feet"},{id:"armor",type:"generic.armor",amount:12.0d,operation:"add_value",slot:"feet"},{id:"armor",type:"generic.armor_toughness",amount:5.0d,operation:"add_value",slot:"feet"}],dyed_color=16777215] 1
execute as @a[tag=shadowF2] if entity @s[scores={reload=110}] run execute as @s at @s run execute as @p[distance=1..10] at @p[distance=1..10] run item replace entity @e[distance=0..10,tag=shadowF1] armor.chest with leather_chestplate[enchantments={levels:{"minecraft:protection":1}},dyed_color=0] 1
execute as @a[tag=shadowF2] if entity @s[scores={reload=110}] run execute as @s at @s run execute as @p[distance=1..10] at @p[distance=1..10] run item replace entity @e[distance=0..10,tag=shadowF1] armor.chest with iron_sword[enchantments={levels:{"minecraft:sharpness":1}}] 1
execute as @a[tag=shadowF2] if entity @s[scores={reload=110}] run execute as @s at @s run tag @p[distance=1..10] add kongju
execute as @a[tag=shadowF2] if entity @s[scores={reload=110}] run execute as @s at @s run playsound minecraft:entity.wither.hurt voice @p[distance=1..10] ~ ~ ~ 1 0.1
execute as @a[tag=shadowF] if entity @s[scores={deathcount=1}] run kill @e[type=minecraft:armor_stand,tag=shadowF1]
execute as @e[type=minecraft:armor_stand,tag=shadowF1] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=shadowF1,scores={reload=700..}] run kill @s
execute as @e[type=armor_stand,tag=shadowF1] run scoreboard players operation @e[type=minecraft:armor_stand,tag=shadowF1] shadowF1 > @e[type=minecraft:armor_stand,tag=shadowF1] reload
execute as @e[type=armor_stand,tag=shadowF1] run scoreboard players operation @s shadowF1 -= @s reload
execute as @e[tag=kongju] at @e[tag=kongju] run particle smoke ~ ~1 ~ 1.0 1.0 1.0 0 2 force
execute as @a[tag=kongju] if entity @s run scoreboard players add @s kongju 1
execute as @a if entity @s[scores={kongju=600..}] run tag @s remove kongju
execute as @a if entity @s[scores={kongju=600..}] run scoreboard players set @s kongju 0



#尼禄
execute as @a[tag=Nero1] run item replace entity @s weapon.mainhand with iron_sword[custom_name='"§4§o绯红女皇"',damage=247,enchantments={levels:{"minecraft:fire_aspect":2}},attribute_modifiers=[{id:"armor",type:"generic.attack_damage",amount:2.0d,operation:"add_value",slot:"mainhand"}]] 1
execute as @a[tag=Nero2] run item replace entity @s hotbar.1 with crossbow[custom_name='"§9§o湛蓝玫瑰"',enchantments={levels:{"minecraft:quick_charge":1,"minecraft:piercing":1}},attribute_modifiers=[{id:"armor",type:"generic.movement_speed",amount:0.1d,operation:"add_multiplied_base",slot:"mainhand"},{id:"armor",type:"generic.movement_speed",amount:0.1d,operation:"add_multiplied_base",slot:"offhand"}]] 1
execute as @a[tag=Nero1] run tag @s remove Nero1
execute as @a[tag=Nero2] run tag @s remove Nero2
execute as @a[tag=Nero] if entity @s run scoreboard players add @s reload 1
execute as @a[tag=Nero,scores={reload=121..}] run tag @s remove FR
execute as @a[tag=Nero,scores={reload=121..}] run tag @s remove FR2
execute as @a[tag=Nero,scores={reload=121..}] run tag @s remove FR3
execute as @a[tag=Nero] if entity @s run scoreboard players add @s ZL 1
execute as @a[tag=Nero,scores={ZL=100}] run tag @s add BL1
#待处理 execute as @a[tag=Nero,scores={ZL=100}] run clear @s minecraft:tipped_arrow
#待处理 execute as @a[tag=Nero,scores={ZL=100}] run give @s minecraft:tipped_arrow{Potion:CBC,CustomPotionEffects:[{Id:2,Amplifier:1,Duration:60}]} 1
execute as @a[tag=Nero,scores={ZL=200}] run tag @s add BL2
#待处理 execute as @a[tag=Nero,scores={ZL=200}] run clear @s minecraft:tipped_arrow
#待处理 execute as @a[tag=Nero,scores={ZL=200}] run give @s minecraft:tipped_arrow{Potion:CBC,CustomPotionEffects:[{Id:2,Amplifier:1,Duration:60},{Id:20,Amplifier:1,Duration:80}]} 1
execute as @a[tag=Nero,scores={ZL=300}] run tag @s add BL3
#待处理 execute as @a[tag=Nero,scores={ZL=300}] run clear @s minecraft:tipped_arrow
#待处理 execute as @a[tag=Nero,scores={ZL=300}] run give @s minecraft:tipped_arrow{Potion:CBC,CustomPotionEffects:[{Id:2,Amplifier:2,Duration:70},{Id:20,Amplifier:2,Duration:90}]} 1
execute as @a[tag=Nero,scores={ZL=500..}] run tag @s remove BL1
execute as @a[tag=Nero,scores={ZL=500..}] run tag @s remove BL2
execute as @a[tag=Nero,scores={ZL=500..}] run tag @s remove BL3
execute as @a[tag=Nero,scores={ZL=500..}] run scoreboard players set @s ZL 0
execute as @a[tag=Nero,scores={ZL=100}] at @s run particle bubble_pop ~ ~1 ~ 1.0 1.0 1.0 0 50 force
execute as @a[tag=Nero,scores={ZL=100}] at @s run playsound minecraft:ambient.underwater.enter voice @s ~ ~ ~ 100 2
execute as @a[tag=Nero,scores={ZL=200}] at @s run particle bubble_pop ~ ~1 ~ 1.0 1.0 1.0 0 80 force
execute as @a[tag=Nero,scores={ZL=200}] at @s run playsound minecraft:ambient.underwater.exit voice @s ~ ~ ~ 100 1
execute as @a[tag=Nero,scores={ZL=300}] at @s run particle bubble_pop ~ ~1 ~ 1.0 1.0 1.0 0 115 force
execute as @a[tag=Nero,scores={ZL=300}] at @s run playsound minecraft:weather.rain voice @s ~ ~ ~ 100 0.7
execute as @e[type=armor_stand,tag=FH] run scoreboard players add @s reload 1
execute as @e[type=armor_stand,tag=FH,scores={reload=100..}] at @s run fill ~13 ~-3 ~-13 ~-13 ~4 ~13 air replace fire
execute as @e[type=armor_stand,tag=FH,scores={reload=100..}] at @s run fill ~13 ~-3 ~-13 ~-13 ~4 ~13 air replace soul_fire
execute as @e[type=armor_stand,tag=FH,scores={reload=101..}] at @s run kill @s

#高斯#
execute as @a[tag=LX] at @s run scoreboard players add @s reload 1
execute as @a[tag=gauss] if entity @s[scores={reload=180}] run tag @s remove LX
execute as @a[tag=gauss] if entity @s[scores={reload=180}] run scoreboard players set @s LX 0
execute as @a[tag=gauss,scores={reload=180}] run scoreboard players set @s reload 0
execute as @a[tag=MH] at @s run scoreboard players add @s MH 1
execute as @a[tag=MH] if entity @s[scores={MH=350}] run give @s diamond_horse_armor[custom_name='"§2马赫冲驰"'] 1
execute as @a[tag=MH] if entity @s[scores={MH=350}] run tag @s remove MH
execute as @a[tag=gauss] if entity @s[scores={MH=350}] run scoreboard players set @s MH 0
execute as @a[scores={LX=4..}] at @s run effect give @s resistance 2 0
execute as @a[scores={LX=4..}] at @s run effect give @a[distance=1..4] slowness 1 1
execute as @a[tag=!XDCY,scores={LX=4..}] at @s run damage @a[limit=1,distance=1..2] 1 fly_into_wall by @p[tag=gauss]
execute as @a[tag=XDCY] at @s run scoreboard players add @s XDCY 1
execute as @a[tag=XDCY] if entity @s[scores={XDCY=199}] run clear @s minecraft:diamond_horse_armor
execute as @a[tag=XDCY] if entity @s[scores={XDCY=200}] run give @s diamond_horse_armor[custom_name='"§2马赫冲驰"'] 1
execute as @a[tag=XDCY] if entity @s[scores={XDCY=200}] run tag @s remove XDCY
execute as @a[tag=gauss] if entity @s[scores={XDCY=200}] run scoreboard players set @s XDCY 0
execute as @a[tag=XDCY] at @s run effect give @s resistance 2 1
execute as @a[tag=XDCY] at @s run effect give @a[distance=1..4] slowness 1 1
execute as @a[tag=XDCY] at @s run damage @a[limit=1,distance=1..3] 2 fly_into_wall by @p[tag=gauss]
execute as @a[tag=XDCY] at @s[scores={XDCY=10}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=20}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=30}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=40}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=60}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=80}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=90}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=100}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=120}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=130}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=140}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=160}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=170}] run summon lightning_bolt ^ ^ ^-4
execute as @a[tag=XDCY] at @s[scores={XDCY=180}] run summon lightning_bolt ^ ^ ^-4
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s XDCY 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s MH 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s LX 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s MHJS 0
execute as @a[scores={LX=4..}] at @s run particle flame ~ ~1 ~ 1.0 1.0 1.0 0 3 force
execute as @a[tag=XDCY] at @s run particle firework ~ ~1 ~ 1.5 1.5 1.5 0 3 force


#拉格纳#
execute as @a[tag=Ragna,scores={health=..30}] at @s run effect give @s minecraft:resistance 2 0


















#击杀奖励
execute as @e[scores={Kills_S=1..}] run scoreboard players set @s Kills_S 0

#地狱传送门和方块设定
execute as @a at @s if block ~ ~-1 ~ minecraft:slime_block run effect give @s jump_boost 1 8

#跑酷设定
execute as @a[tag=QinKit_Parkour] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run title @s actionbar "§e§l重生点已保存！"
execute as @a[tag=QinKit_ParkourExtra] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run title @s actionbar "§e§l重生点已保存！"

execute as @a[tag=QinKit_Parkour] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 1 0.5
execute as @a[tag=QinKit_ParkourExtra] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 1 0.5

execute as @a[tag=QinKit_Parkour] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run spawnpoint @s ~ ~ ~
execute as @a[tag=QinKit_ParkourExtra] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run spawnpoint @s ~ ~ ~

execute as @a[tag=QinKit_Parkour] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run tag @s add spawnpoint
execute as @a[tag=QinKit_ParkourExtra] at @s if entity @s[tag=!spawnpoint] if block ~ ~-1 ~ minecraft:gold_block run tag @s add spawnpoint

execute as @a[tag=spawnpoint] at @s unless block ~ ~-1 ~ minecraft:gold_block run tag @s remove spawnpoint

#全局设定
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s reload 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s passiveReload 0
execute as @a if entity @s[scores={deathcount=1..}] run scoreboard players set @s deathcount 0

execute as @a if entity @s[scores={quit=1..}] run scoreboard players set @s reload 0
execute as @a if entity @s[scores={quit=1..}] run scoreboard players set @s passiveReload 0
execute as @a if entity @s[scores={quit=1..}] run scoreboard players set @s quit 0

execute as @a at @s if entity @s[y=-80,dy=3] run kill @s

execute as @a run scoreboard players set @s M 0
execute as @a run scoreboard players set @s S 0

#悦灵战争设定
execute as @e[type=minecraft:allay,tag=!pvz_plant,tag=!dont_look] at @s facing entity @e[distance=1..15,sort=nearest,limit=1,type=!minecraft:arrow,type=!minecraft:item,type=!minecraft:allay] eyes run tp @s ~ ~ ~ ~ ~20
execute as @e[tag=allay_damage,type=arrow,nbt={inGround:1b}] run kill @s
execute as @e[tag=allay_gold,type=item] run scoreboard players add @s reload 1
execute as @e[tag=allay_gold,type=item] if entity @s[scores={reload=1200..}] run kill @s

execute as @e[type=minecraft:villager,tag=aw_shop] at @s anchored eyes facing entity @p[distance=1..8,sort=nearest,limit=1] eyes run tp @s ~ ~ ~ ~ ~
execute as @e[tag=stand_npc] at @s anchored eyes facing entity @p[distance=1..8,sort=nearest,limit=1] eyes run tp @s ~ ~ ~ ~ ~
execute as @e[type=minecraft:piglin_brute,tag=hell_pig] at @s run data merge entity @s {TimeInOverworld:20}

#植物大战僵尸设定
execute as @e[tag=plant_bullet] run scoreboard players add @s reload 1
execute as @e[tag=plant_bullet] if entity @s[scores={reload=80..}] run kill @s
execute as @e[tag=ice_pea_bullet] at @s run particle minecraft:snowflake ~ ~ ~ 0 0 0 0 1

execute as @e[tag=potatoMine] at @s if entity @s[tag=mine_ready,tag=!ready_up] run tag @s add ready_up
execute as @e[tag=potatoMine] at @s if entity @s[tag=ready_up,tag=!up_end] run tp @s ~ ~0.55 ~
execute as @e[tag=potatoMine] at @s if entity @s[tag=ready_up,tag=!up_end] run tag @s add up_end
execute as @e[tag=potatoMine] at @s if entity @s[tag=ready_up] run tag @s remove ready_up

execute as @e[tag=pvz_zombie,tag=!persist_mob] run data merge entity @s {PersistenceRequired:1b}
execute as @e[tag=allay_army,tag=!persist_mob] run data merge entity @s {PersistenceRequired:1b}
execute as @e[tag=pvz_zombie,tag=!persist_mob] run tag @s add persist_mob
execute as @e[tag=pvz_grave] run team join noCol @s
execute as @e[tag=refresh_walk] at @s run tp @s ~ ~ ~
execute as @e[tag=refresh_walk] at @s run tag @s remove refresh_walk

execute as @e[tag=graveEater] at @s run tp @s ~ ~-0.015 ~

execute as @e[tag=pvz_dreamed] at @s run particle minecraft:heart ~ ~2 ~ 0.25 0.25 0.25 0 1 force