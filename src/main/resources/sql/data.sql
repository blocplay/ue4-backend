INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(0, 0, 'Test', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');  
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(2, 2, 'Player1', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(3, 3, 'Player2', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(4, 4, 'Player3', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(5, 5, 'Player4', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(6, 6, 'Player5', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(7, 7, 'Player6', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(8, 8, 'Player7', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
INSERT INTO profiles(id, user_id, callsign, bio, location, created_at, updated_at)
  VALUES(9, 9, 'Player8', NULL, NULL, '2013-02-15 12:03:43.0', '2013-02-15 12:03:43.0');
--
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(0, 0, 'test', '$2a$10$jxS.RE4yOGLwAgNnLcWpheXBjhpcIyjOOAIchYeKDBrz2sRfKLT/u', 1, 1, 4, 'Test Pilot', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(2, 2, 'player1', '$2a$10$zmNESjNc7xlbLKCCClr3t.dqYnjcZYhlqp.O20QO8LN7GmEviZega', 0, 1, 4, 'Player 1', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(3, 3, 'player2', '$2a$10$9hApOlIBH2187A8pHX88ReDPs.clkX2WSt1jSlxAJxOr/r6otm.WW', 0, 1, 4, 'Player 2', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(4, 4, 'player3', '$2a$10$gjYBiKDrTxdgPS9UewlgaOwu0QxG3djK4xiM9UFrF8dHd3hCdDI02', 0, 1, 4, 'Player 3', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(5, 5, 'player4', '$2a$10$C9ziuH/Ixg5eaLlkk6e2BOBlwe5k6zfXWb3Yn4AHo8USQX5zLD1Ru', 0, 1, 4, 'Player 4', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(6, 6, 'player5', '$2a$10$0dsB3T3fqFiMSnasTNZrb.KF/WqQWGWcm.Ye8q/TbuCBKSQ7s7dsK', 0, 1, 4, 'Player 5', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(7, 7, 'player6', '$2a$10$INsjOxrnwqYyD2jrTxTtl.pkkwJwingpVcWuVC.YMaErJX2eIckc2', 0, 1, 4, 'Player 6', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(8, 8, 'player7', '$2a$10$fyEsctpc6EnI6j4PrCueguVGzsZYfRBpYZbVfXkgh5BPe/pM65/cy', 0, 1, 4, 'Player 7', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
INSERT INTO users(id, profile_id, email, password, role, active, role_id, firstname, lastname, language, deleted, created_at, updated_at, remember_token, referred_by_code, confirmation_code, mod, alpha, nda, eula)
  VALUES(9, 9, 'player8', '$2a$10$3SEZrZqyPLnaHftQ4leusuSBmz2hROdDRQq63GXe4HwIT4CJu986S', 0, 1, 4, 'Player 8', 'User', 'en', 0, '2013-02-15 12:03:43.0', '2015-05-13 19:33:47.0', NULL, '0', NULL, NULL, 1, 1, NULL);
--
INSERT INTO tue4_pilot (PIL_ID, PIL_CALLSIGN, PIL_USU_ID, PIL_DISABLE_CHAT, PIL_DISABLE_REQUESTS, PIL_OFF_LIMITS, PIL_USE_CUSTOM_SCHEME)
  VALUES ('59a3e59c622d4dc4be01e3d094d2341a', 'Test', '0', '0', '0', '0', '0');
--
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_CharacterTest', '/Game/Maps/Test/HG_CharacterTest');
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_ChopShop', '/Game/Maps/Arenas/ChopShop/HG_ChopShop');
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_HydroStation', '/Game/Maps/Arenas/HydroStation/HG_HydroStation');
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_PioneerOutpost', '/Game/Maps/Arenas/PioneerBravoOutpost/HG_PioneerOutpost');
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_PortOasis', '/Game/Maps/Arenas/PortOasis/HG_PortOasis');
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_SandDrift', '/Game/Maps/Arenas/SandDrift/HG_SandDrift');
INSERT INTO tue4_map(MAP_ID, MAP_ASSET_NAME)
  VALUES('HG_SanLopez', '/Game/Maps/Arenas/SanLopezMines/HG_SanLopez');
--
INSERT INTO tue4_game_mode(GAM_ID, GAM_ASSET_NAME)
  VALUES('BrunesBall_C', '/Game/Data/Blueprint/Gametype/BrunesBall.BrunesBall_C');
INSERT INTO tue4_game_mode(GAM_ID, GAM_ASSET_NAME)
  VALUES('CaptureTheFlag_C', '/Game/Data/Blueprint/Gametype/CaptureTheFlag.CaptureTheFlag_C');
INSERT INTO tue4_game_mode(GAM_ID, GAM_ASSET_NAME)
  VALUES('Deathmatch_C', '/Game/Data/Blueprint/Gametype/Deathmatch.Deathmatch_C');
INSERT INTO tue4_game_mode(GAM_ID, GAM_ASSET_NAME)
  VALUES('Domination_C', '/Game/Data/Blueprint/Gametype/Domination.Domination_C');
INSERT INTO tue4_game_mode(GAM_ID, GAM_ASSET_NAME)
  VALUES('TeamDeathMatch_C', '/Game/Data/Blueprint/Gametype/TeamDeathMatch.TeamDeathMatch_C');
INSERT INTO tue4_game_mode(GAM_ID, GAM_ASSET_NAME)
  VALUES('WaveSurvival_C', '/Game/Data/Blueprint/Gametype/WaveSurvival.WaveSurvival_C');
--
INSERT INTO tue4_ui_attribute(UAT_ID, UAT_NAME, UAT_DESCRIPTION)
  VALUES('font-data-color', 'font-data-color', NULL);
INSERT INTO tue4_ui_attribute(UAT_ID, UAT_NAME, UAT_DESCRIPTION)
  VALUES('light-color', 'light-color', NULL);
INSERT INTO tue4_ui_attribute(UAT_ID, UAT_NAME, UAT_DESCRIPTION)
  VALUES('lightbrilliant-color', 'lightbrilliant-color', NULL);
INSERT INTO tue4_ui_attribute(UAT_ID, UAT_NAME, UAT_DESCRIPTION)
  VALUES('main-color', 'main-color', NULL);
INSERT INTO tue4_ui_attribute(UAT_ID, UAT_NAME, UAT_DESCRIPTION)
  VALUES('personal-color', 'personal-color', NULL);
INSERT INTO tue4_ui_attribute(UAT_ID, UAT_NAME, UAT_DESCRIPTION)
  VALUES('system-color', 'system-color', NULL);
--
INSERT INTO tue4_ui_theme(UTH_ID, UTH_NAME, UTH_DESCRIPTION)
  VALUES('default', 'Default', 'Default blueish theme');
INSERT INTO tue4_ui_theme(UTH_ID, UTH_NAME, UTH_DESCRIPTION)
  VALUES('inferno', 'Inferno', NULL);
INSERT INTO tue4_ui_theme(UTH_ID, UTH_NAME, UTH_DESCRIPTION)
  VALUES('neongreen', 'NeonGreen', NULL);
--
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('font-data-color-default', 'default', 'font-data-color', 'White');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('font-data-color-inferno', 'inferno', 'font-data-color', 'Gold');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('font-data-color-neongreen', 'neongreen', 'font-data-color', '#F5F6CE');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('light-default', 'default', 'light-color', '#A0EAFF');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('light-inferno', 'inferno', 'light-color', '#980000');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('light-neongreen', 'neongreen', 'light-color', 'DarkGreen');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('lightbrilliant-default', 'default', 'lightbrilliant-color', '#A0EAFF');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('lightbrilliant-inferno', 'inferno', 'lightbrilliant-color', 'Gold');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('lightbrilliant-neongreen', 'neongreen', 'lightbrilliant-color', '#A1FFC1');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('main-default', 'default', 'main-color', '#2A9FD6');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('main-inferno', 'inferno', 'main-color', '#B80000');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('main-neongreen', 'neongreen', 'main-color', '#3C0');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('personal-color-default', 'default', 'personal-color', 'Gold');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('personal-color-inferno', 'inferno', 'personal-color', '#9F0');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('personal-color-neongreen', 'neongreen', 'personal-color', 'Gold');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('system-color-default', 'default', 'system-color', 'Tomato');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('system-color-inferno', 'inferno', 'system-color', '#33F');
INSERT INTO tue4_ui_theme_attribute(UTA_ID, UTA_UTH_ID, UTA_UAT_ID, UTA_VALUE)
  VALUES('system-color-neongreen', 'neongreen', 'system-color', 'Tomato');
--  
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('0a8fb77dac40488f8ab5e52469f5a769', 'RightHand');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('0ba481b8ebb54333b5988a80d556aa4d', 'LeftHand');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('1', 'FuelTankStorage');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('15d177aae8824a209b4b4d7e1cf82f14', 'RightCollarMount');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('2', 'LeftCollarMount');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('524401a5caa747529f2a7ccc4ba9da91', 'UpperLegOutLeft');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('73041467807c4c9e9b7b5284e806ffac', 'LowerArmLeft');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('d23e8560a30a45c1b5078cef23c17f9b', 'HipStorage');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('d405f183e7ea4bf2a3bc9ddfb38a3ba5', 'UpperLegOutRight');
INSERT INTO tue4_inventory_location(INL_ID, INL_NAME)
  VALUES('e139a2a47795489e94a99e6115799c60', 'LowerArmRightMount');
--
INSERT INTO tue4_inventory_object(INO_ID, INO_NAME, INO_BLU_MASTER)
  VALUES('1', 'Light AutoCannon ', '/Game/Data/Characters/Gears/Weapons/Guns/LightAutoCannon/GearLightAutoCannon001.GearLightAutoCannon001_C');
INSERT INTO tue4_inventory_object(INO_ID, INO_NAME, INO_BLU_MASTER)
  VALUES('2', 'Very Light Rocket Pod 32', '/Game/Data/Characters/Gears/Weapons/Missiles/VeryLightRocketPod/VeryLightRocketPod32_001.VeryLightRocketPod32_001_C');
INSERT INTO tue4_inventory_object(INO_ID, INO_NAME, INO_BLU_MASTER)
  VALUES('31674b9c00e543a2bb799daab72242ee', 'SubMachineGun', '/Game/Data/Characters/Gears/Weapons/Guns/SubMachineGun/Gear_SubMachineGun_001.Gear_SubMachineGun_001_C');
INSERT INTO tue4_inventory_object(INO_ID, INO_NAME, INO_BLU_MASTER)
  VALUES('6829bf965cbc4578a3c82961718340af', 'Saggittarius Maingun', '/Game/Data/Characters/Strider/Weapons/Saggittarius_maingun.Saggittarius_maingun_C');
--
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('02019c271eef4a3f808a25d4b979a0eb', 'Jaeger_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('027443d22ace4441b49602e15e19f82a', 'Jaeger_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('07cc52f4c9a74140b34890c14eaf3949', 'Tiger_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('0898c1464144452f98e3544ddb484103', 'Jaeger_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('0ac1d760c4db417e96534f1e5a4e1ed1', 'Tiger_LeftLowerLeg', 'LOWER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('0be2c71438b0424f82eb24d731de4e3b', 'Tiger_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('0cf83b2b1b794f20adb68b397b3de74b', 'BlackMamba_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('0d1454e58de04de4a5cd50247e7a0ddf', 'Hunter_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('10a2433d577140c0a4e41e177747c74c', 'Jaguar_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('12934233948a4499a8af1378a24b4b42', 'Warrior_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('12a465e84d95496eb9a330705ec65341', 'Hunter_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('12cf34aed6914d31af42c84a1648e8de', 'Hunter_LeftLowerLeg', 'LOWER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('12f53c9596744976a3f3a54f55c1ee3f', 'Cheetah_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('19058e19aed34b38a2d735a311e3808c', 'GearEngineLPylon', 'PYLON_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('1a4b0229004d420f9ab332116623b546', 'Jaguar_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('1b394f0945a44db48339f3160d669646', 'SideWinder_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('1d8e667ff770463da0051bdc9a9118f1', 'Cheetah_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('1db2c0492d8b496baa8013c2fc9a2062', 'GearEngineRPylon', 'PYLON_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('21733719927745f29e0e09c23467d9db', 'Cheetah_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('230acd3cd4ec4e0ba3ae03c58fb4245b', 'Cheetah_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('28218b00e0a04fb8a73bba744cf1e90b', 'BlackMamba_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('28270dca9695456f8c6293141568594d', 'BlackMamba_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('37a46752e3f94201aeb74a9bb5071d1b', 'Tiger_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('3864b8c5668646f1b18dae42ce1d5d8d', 'Hunter_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('397c6f3edf5e450ca451e379d9c79a0a', 'Archetype_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('3a6b59f810544c81bf2a2d9d3b12ba65', 'Tiger_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('3b44821876d148fbb34de834de523c3f', 'Hunter_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('3c45b8fce4284b11b1febd0d4af26461', 'Tiger_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('3ec1e66b4f91440bb5026c5ed0a80a35', 'Jaguar_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('3edea7e514c043c2a618dbfc4e7ad943', 'Jaeger_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('452464df5bb94130ab03859f5e183989', 'Jaguar_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('4c7d15f7361b49e09b54e312918fce7a', 'BlackMamba_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('4d2e3c1da8a4431fb3fb5c258e36dcb6', 'Jaeger_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('4fa535e655cd463eb87b47f7634ccffd', 'Jaguar_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5141e58dc22149b79de62036aec5b726', 'Hunter_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5225f1160a784f6e9b82f8c07d794fd8', 'Jaeger_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('53217127c5c548798ad91dedd2429e95', 'SideWinder_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('532b5b422b214fe7a8267d90cda8f814', 'Hunter_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5364e2a5b1ce4476b0bb495ab2a50c1e', 'Tiger_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('558e33057e4148da9e5a076d4eca56fb', 'Jaguar_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5a85f2d2048b4c138b368427870a680a', 'BlackMamba_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5b9f746544bf4902bcadfe78e5044f1d', 'Cheetah_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5dfde6200c4846158d3cda2aaa69ff93', 'Cheetah_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('5f88e7ea200c4b33aac4ad8a38aa9161', 'Cheetah_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('61580c6da1544574803d2396799f9c22', 'Tiger_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('62ce26e5b80e41ca85fac1d44dd78750', 'Jaguar_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('67f3317d6d0540d9bf86d93b55613954', 'Jaeger_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('6879eb1a239947fa96829486f979903c', 'Cheetah_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('68ad24aab4f04c0fb7ee4a81bcf3c478', 'GearEngineCore', 'ENGINE');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('6d40778737714c3d94a1096eb51b2dd6', 'Cheetah_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('70cdce70d44b446a8c88d124e2470292', 'Jaguar_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('719dbef637ca49f7999982b6317aef79', 'SideWinder_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('72fdd8cdc81240bf8484ff55c44c5b69', 'Hunter_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('75d0dbceb96d4a2a95a894772dfd0355', 'Cheetah_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('7656261df93c4847a292d9effa51b0d2', 'SideWinder_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('76c7ef24da2f4125ada5ca3605f95a77', 'BlackMamba_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('7848e24249734730a159fc8a5a921c89', 'Jaguar_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('790401d4d3bf485182e20853f2e074b6', 'SideWinder_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('79b4023c0ff742f0801f830ada666300', 'BlackMamba_LeftLowerLeg', 'LOWER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('7b36ef8a4e9e498c90abf073cdd54057', 'BlackMamba_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('7b8a49adbfc8448dae08d342052c000c', 'Jaeger_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('7c46c267e67942a2a864e452b4e53a1c', 'SideWinder_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('7d2fd81b33d5428c818f4eeced636c13', 'BlackMamba_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('800158a7ef224fb0b2e4fa528659dfc1', 'SideWinder_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('800535dd81844b01901b395e38e0d165', 'SideWinder_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('8061150591da47079611fbd3f865495e', 'BlackMamba_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('81e9488dbcc34f41ba5363a7a0417efc', 'Tiger_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('84135f9acefe436d9befa3454c571bc8', 'Archetype_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('8e4f980df4dc47618d9f9970226b350a', 'SideWinder_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('8ec002737d6a4d5eba8bb15be858c9ef', 'Tiger_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('8fc3210e8cc940c890ce008ca3aed743', 'Hunter_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('902d150932e1459aa721033f125a268b', 'Archetype_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('90b93174a7ca43559d186d77beb01538', 'Hunter_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('93a4fc7183a546e6bab8aa7e2849851a', 'Cheetah_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('95211081b5084cc7b58df6a88e68a578', 'BlackMamba_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('978eca728aba420989575c8d51ac5e15', 'Tiger_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('9cde198cb531414fafbf1dc788dbbd59', 'Jaeger_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('9e468c49d880455b95179ff0ab7aecd9', 'Hunter_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('a1700c07b79b4164b8c393904136b661', 'Jaeger_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('a5b026ed9fcd4bca965ed609d11c51bf', 'BlackMamba_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('a84f040892f54ddcbe40ca556de96a7c', 'Archetype_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('a8552fbdd47746e18520ac470c06d6d2', 'BlackMamba_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('a9316778bf8940d8ac24ba00c8b97483', 'SideWinder_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('aa54f1d52c3d4a4c896cfc75731fd2ba', 'Tiger_LeftShoulderPad', 'SHOULDER_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('aa5d30d262ca4ab5b2ad253f3f2136cd', 'Cheetah_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('aa7b530f7d5b476b9d9fbaf0c34c1b6b', 'GearEngineFuelTank', 'FUEL_TANK');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('aac8fbdc03f94e7ca91e61115f3f97b8', 'SideWinder_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('ac27577a859444dcacb82d5703c2db91', 'Jaeger_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('aefbe71b4b9c4bd3a9416128c9aec03b', 'Jaeger_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('b4755c2d14ab4cf78cdbd528848b8e1d', 'Jaeger_RightFoot', 'FOOT_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('b85f85c12eb94f519f079a806ac1d7f4', 'SideWinder_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('ba77cebe80cb496aae6bd4e8a6cb0d26', 'Cheetah_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('bde509c074264813bd247bec778b0f09', 'Jaguar_LeftLowerLeg', 'LOWER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('be6f5c55eb6641e8bf19d3cd8413c000', 'Hunter_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c07e723b68ac4a3f82a9bfe425cfe16a', 'Tiger_RightUpperLeg', 'UPPER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c5ca86da3cdc4a2dae71f072968e0356', 'Tiger_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c7098809b3994970b65f37f95dd86971', 'Jaeger_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c7c84f9b63864531a2bde32b37aa863f', 'Cheetah_Head', 'HEAD');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c807851599784536acd88c1d25bcaa63', 'Hunter_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c8249377e2314143b7121fd5ed62c231', 'Jaeger_LeftLowerLeg', 'LOWER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('c945450430664b1fa3d7dfd8fc9aa556', 'BlackMamba_LeftHand', 'HAND_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('cb36fd2c5302442190c7373c871e4bd6', 'Jaguar_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('d2921fd9fdf54a748fdbd90217c06a62', 'Jaeger_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('d62a79267db04fd7a7ba5a26d2f4100b', 'Hunter_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('d6a8e4953c954005a0a1c5d417704c9c', 'Jaeger_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('d6f7647ed7394c139c5fec5cdd422b43', 'Cheetah_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('d86d81faf05e459883f4d2279ad916e0', 'SideWinder_LeftUpperArm', 'UPPER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('d9a2adf8120f431f991b5eaef667b1a6', 'Hunter_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('da99c656386d4f449e2bfd8956c68734', 'SideWinder_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('dabe4ed4c8044d5b948a39df89b630da', 'SideWinder_LeftLowerLeg', 'LOWER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('db27f04065f94165b8fe730a13cd851e', 'SideWinder_LeftLowerArm', 'LOWER_ARM_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('dbc78f2c3151400bb0809eb5f426c2f9', 'Tiger_LeftFoot', 'FOOT_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('ddacef4a6c2445f9a13de0f94bc2a3da', 'BlackMamba_RightLowerLeg', 'LOWER_LEG_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('e9ba71a54f80451d888124818a68d059', 'SideWinder_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('eabe5318db45497187268260e548e5af', 'Cheetah_Torso', 'TORSO');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('eacdedaae74447c3a85fe232c3c7faba', 'Jaguar_RightShoulderPad', 'SHOULDER_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('eae5d80d8a15498f938a13dbf85e30f2', 'Tiger_RightUpperArm', 'UPPER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('efd7381d15884792ba4413f7959bc64d', 'BlackMamba_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('f298ff2647c345d191b02d04e202fe09', 'Jaguar_LeftUpperLeg', 'UPPER_LEG_LEFT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('f31b53bb64b74ef9be1fabe77cd120ea', 'BlackMamba_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('f418057773e3490d8bbb6c45ad16c4a0', 'Tiger_Hip', 'HIP');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('f605c39a332847bbb7accd95077c3f4a', 'Hunter_RightHand', 'HAND_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('f718c538a15d4bfc8532e4e78677e4d3', 'Hunter_RightLowerArm', 'LOWER_ARM_RIGHT');
INSERT INTO tue4_gear_section(GES_ID, GES_NAME, GES_TYPE)
  VALUES('fcb42a42c94447ef87275553ccb24023', 'Cheetah_LeftLowerLeg', 'LOWER_LEG_LEFT');  
--
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('1', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'Hunter_Head', 'Hunter_Hip', 'Hunter_Torso', 'Hunter_RightFoot', 'Hunter_LeftFoot', 'Hunter_RightUpperLeg', 'Hunter_LeftUpperLeg', 'Hunter_RightLowerLeg', 'Hunter_LeftLowerLeg', 'Hunter_RightHand', 'Hunter_LeftHand', 'Hunter_RightLowerArm', 'Hunter_LeftLowerArm', 'Hunter_RightUpperArm', 'Hunter_LeftUpperArm', 'Hunter_RightShoulderPad', 'Hunter_LeftShoulderPad', 'Hunter', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', '12a465e84d95496eb9a330705ec65341', 'be6f5c55eb6641e8bf19d3cd8413c000', '9e468c49d880455b95179ff0ab7aecd9', 'c807851599784536acd88c1d25bcaa63', '3b44821876d148fbb34de834de523c3f', '12cf34aed6914d31af42c84a1648e8de', 'f605c39a332847bbb7accd95077c3f4a', '72fdd8cdc81240bf8484ff55c44c5b69', 'f718c538a15d4bfc8532e4e78677e4d3', '532b5b422b214fe7a8267d90cda8f814', '5141e58dc22149b79de62036aec5b726', '90b93174a7ca43559d186d77beb01538', 'd9a2adf8120f431f991b5eaef667b1a6', '8fc3210e8cc940c890ce008ca3aed743', '3864b8c5668646f1b18dae42ce1d5d8d', 'd62a79267db04fd7a7ba5a26d2f4100b', '0d1454e58de04de4a5cd50247e7a0ddf');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('2', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'Jaeger_Head', 'Jaeger_Hip', 'Jaeger_Torso', 'Jaeger_RightFoot', 'Jaeger_LeftFoot', 'Jaeger_RightUpperLeg', 'Jaeger_LeftUpperLeg', 'Jaeger_RightLowerLeg', 'Jaeger_LeftLowerLeg', 'Jaeger_RightHand', 'Jaeger_LeftHand', 'Jaeger_RightLowerArm', 'Jaeger_LeftLowerArm', 'Jaeger_RightUpperArm', 'Jaeger_LeftUpperArm', 'Jaeger_RightShoulderPad', 'Jaeger_LeftShoulderPad', 'Jaeger', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', 'b4755c2d14ab4cf78cdbd528848b8e1d', '5225f1160a784f6e9b82f8c07d794fd8', '4d2e3c1da8a4431fb3fb5c258e36dcb6', 'ac27577a859444dcacb82d5703c2db91', 'a1700c07b79b4164b8c393904136b661', 'c8249377e2314143b7121fd5ed62c231', 'd2921fd9fdf54a748fdbd90217c06a62', '3edea7e514c043c2a618dbfc4e7ad943', '9cde198cb531414fafbf1dc788dbbd59', 'd6a8e4953c954005a0a1c5d417704c9c', '7b8a49adbfc8448dae08d342052c000c', '0898c1464144452f98e3544ddb484103', 'aefbe71b4b9c4bd3a9416128c9aec03b', '02019c271eef4a3f808a25d4b979a0eb', '67f3317d6d0540d9bf86d93b55613954', '027443d22ace4441b49602e15e19f82a', 'c7098809b3994970b65f37f95dd86971');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('241b541cf3d14427b1e688414fe16f5d', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'Tiger_Head', 'Tiger_Hip', 'Tiger_Torso', 'Tiger_RightFoot', 'Tiger_LeftFoot', 'Tiger_RightUpperLeg', 'Tiger_LeftUpperLeg', 'Tiger_RightLowerLeg', 'Tiger_LeftLowerLeg', 'Tiger_RightHand', 'Tiger_LeftHand', 'Tiger_RightLowerArm', 'Tiger_LeftLowerArm', 'Tiger_RightUpperArm', 'Tiger_LeftUpperArm', 'Tiger_RightShoulderPad', 'Tiger_LeftShoulderPad', 'Tiger', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', '61580c6da1544574803d2396799f9c22', 'dbc78f2c3151400bb0809eb5f426c2f9', 'c07e723b68ac4a3f82a9bfe425cfe16a', '5364e2a5b1ce4476b0bb495ab2a50c1e', '0be2c71438b0424f82eb24d731de4e3b', '0ac1d760c4db417e96534f1e5a4e1ed1', '3a6b59f810544c81bf2a2d9d3b12ba65', '978eca728aba420989575c8d51ac5e15', '81e9488dbcc34f41ba5363a7a0417efc', '37a46752e3f94201aeb74a9bb5071d1b', 'eae5d80d8a15498f938a13dbf85e30f2', '3c45b8fce4284b11b1febd0d4af26461', 'c5ca86da3cdc4a2dae71f072968e0356', 'aa54f1d52c3d4a4c896cfc75731fd2ba', '07cc52f4c9a74140b34890c14eaf3949', 'f418057773e3490d8bbb6c45ad16c4a0', '8ec002737d6a4d5eba8bb15be858c9ef');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('6b031d46e42c479592213cd6f7c7ebcc', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'Warrior_Head', 'Jaguar_Hip', 'Hunter_Torso', 'Hunter_RightFoot', 'Hunter_LeftFoot', 'Hunter_RightUpperLeg', 'Hunter_LeftUpperLeg', 'Hunter_RightLowerLeg', 'Hunter_LeftLowerLeg', 'Hunter_RightHand', 'Hunter_LeftHand', 'Jaguar_RightLowerArm', 'Jaguar_LeftLowerArm', 'Jaguar_RightUpperArm', 'Jaguar_LeftUpperArm', 'BlackMamba_RightShoulderPad', 'BlackMamba_LeftShoulderPad', 'Warrior', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', '12a465e84d95496eb9a330705ec65341', 'be6f5c55eb6641e8bf19d3cd8413c000', '9e468c49d880455b95179ff0ab7aecd9', 'c807851599784536acd88c1d25bcaa63', '3b44821876d148fbb34de834de523c3f', '12cf34aed6914d31af42c84a1648e8de', 'f605c39a332847bbb7accd95077c3f4a', '72fdd8cdc81240bf8484ff55c44c5b69', '10a2433d577140c0a4e41e177747c74c', '558e33057e4148da9e5a076d4eca56fb', '1a4b0229004d420f9ab332116623b546', 'cb36fd2c5302442190c7373c871e4bd6', '76c7ef24da2f4125ada5ca3605f95a77', '7d2fd81b33d5428c818f4eeced636c13', '12934233948a4499a8af1378a24b4b42', '4fa535e655cd463eb87b47f7634ccffd', '0d1454e58de04de4a5cd50247e7a0ddf');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('6f0a7289f4c24c7d9465b01e49698303', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'BlackMamba_Head', 'BlackMamba_Hip', 'BlackMamba_Torso', 'BlackMamba_RightFoot', 'BlackMamba_LeftFoot', 'BlackMamba_RightUpperLeg', 'BlackMamba_LeftUpperLeg', 'BlackMamba_RightLowerLeg', 'BlackMamba_LeftLowerLeg', 'BlackMamba_RightHand', 'BlackMamba_LeftHand', 'BlackMamba_RightLowerArm', 'BlackMamba_LeftLowerArm', 'BlackMamba_RightUpperArm', 'BlackMamba_LeftUpperArm', 'BlackMamba_RightShoulderPad', 'BlackMamba_LeftShoulderPad', 'BlackMamba', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', '4c7d15f7361b49e09b54e312918fce7a', 'a8552fbdd47746e18520ac470c06d6d2', '8061150591da47079611fbd3f865495e', '28218b00e0a04fb8a73bba744cf1e90b', 'ddacef4a6c2445f9a13de0f94bc2a3da', '79b4023c0ff742f0801f830ada666300', 'efd7381d15884792ba4413f7959bc64d', 'c945450430664b1fa3d7dfd8fc9aa556', 'a5b026ed9fcd4bca965ed609d11c51bf', '5a85f2d2048b4c138b368427870a680a', '28270dca9695456f8c6293141568594d', '95211081b5084cc7b58df6a88e68a578', '76c7ef24da2f4125ada5ca3605f95a77', '7d2fd81b33d5428c818f4eeced636c13', '0cf83b2b1b794f20adb68b397b3de74b', 'f31b53bb64b74ef9be1fabe77cd120ea', '7b36ef8a4e9e498c90abf073cdd54057');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('9cde3f6ec7e046aabac618a7332ce0fe', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'SideWinder_Head', 'SideWinder_Hip', 'SideWinder_Torso', 'SideWinder_RightFoot', 'SideWinder_LeftFoot', 'SideWinder_RightUpperLeg', 'SideWinder_LeftUpperLeg', 'SideWinder_RightLowerLeg', 'SideWinder_LeftLowerLeg', 'SideWinder_RightHand', 'SideWinder_LeftHand', 'SideWinder_RightLowerArm', 'SideWinder_LeftLowerArm', 'SideWinder_RightUpperArm', 'SideWinder_LeftUpperArm', 'SideWinder_RightShoulderPad', 'SideWinder_LeftShoulderPad', 'SideWinder', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', 'aac8fbdc03f94e7ca91e61115f3f97b8', '53217127c5c548798ad91dedd2429e95', '7656261df93c4847a292d9effa51b0d2', '790401d4d3bf485182e20853f2e074b6', 'b85f85c12eb94f519f079a806ac1d7f4', 'dabe4ed4c8044d5b948a39df89b630da', '8e4f980df4dc47618d9f9970226b350a', '1b394f0945a44db48339f3160d669646', 'da99c656386d4f449e2bfd8956c68734', 'db27f04065f94165b8fe730a13cd851e', '800158a7ef224fb0b2e4fa528659dfc1', 'd86d81faf05e459883f4d2279ad916e0', '7c46c267e67942a2a864e452b4e53a1c', '719dbef637ca49f7999982b6317aef79', 'a9316778bf8940d8ac24ba00c8b97483', 'e9ba71a54f80451d888124818a68d059', '800535dd81844b01901b395e38e0d165');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('c6a83effb47445cbaa6bf9fe9aa128b0', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'Cheetah_Head', 'Cheetah_Hip', 'Cheetah_Torso', 'Cheetah_RightFoot', 'Cheetah_LeftFoot', 'Cheetah_RightUpperLeg', 'Cheetah_LeftUpperLeg', 'Cheetah_RightLowerLeg', 'Cheetah_LeftLowerLeg', 'Cheetah_RightHand', 'Cheetah_LeftHand', 'Cheetah_RightLowerArm', 'Cheetah_LeftLowerArm', 'Cheetah_RightUpperArm', 'Cheetah_LeftUpperArm', 'Cheetah_RightShoulderPad', 'Cheetah_LeftShoulderPad', 'Cheetah', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', '5b9f746544bf4902bcadfe78e5044f1d', 'd6f7647ed7394c139c5fec5cdd422b43', '6879eb1a239947fa96829486f979903c', '1d8e667ff770463da0051bdc9a9118f1', '230acd3cd4ec4e0ba3ae03c58fb4245b', 'fcb42a42c94447ef87275553ccb24023', '21733719927745f29e0e09c23467d9db', 'ba77cebe80cb496aae6bd4e8a6cb0d26', 'aa5d30d262ca4ab5b2ad253f3f2136cd', '5f88e7ea200c4b33aac4ad8a38aa9161', '5dfde6200c4846158d3cda2aaa69ff93', '75d0dbceb96d4a2a95a894772dfd0355', '93a4fc7183a546e6bab8aa7e2849851a', '12f53c9596744976a3f3a54f55c1ee3f', 'c7c84f9b63864531a2bde32b37aa863f', '6d40778737714c3d94a1096eb51b2dd6', 'eabe5318db45497187268260e548e5af');
INSERT INTO tue4_gear_model(GEM_ID, GEM_BLU_MASTER, GEM_BLU_ENGINE, GEM_BLU_FUEL_TANK, GEM_BLU_PYLON_RIGHT, GEM_BLU_PYLON_LEFT, GEM_BLU_HEAD, GEM_BLU_HIP, GEM_BLU_TORSO, GEM_BLU_FOOT_RIGHT, GEM_BLU_FOOT_LEFT, GEM_BLU_UPPER_LEG_RIGHT, GEM_BLU_UPPER_LEG_LEFT, GEM_BLU_LOWER_LEG_RIGHT, GEM_BLU_LOWER_LEG_LEFT, GEM_BLU_HAND_RIGHT, GEM_BLU_HAND_LEFT, GEM_BLU_LOWER_ARM_RIGHT, GEM_BLU_LOWER_ARM_LEFT, GEM_BLU_UPPER_ARM_RIGHT, GEM_BLU_UPPER_ARM_LEFT, GEM_BLU_SHOULDER_RIGHT, GEM_BLU_SHOULDER_LEFT, GEM_NAME, GEM_SELECTABLE, GEM_GES_ENGINE, GEM_GES_FUEL_TANK, GEM_GES_PYLON_RIGHT, GEM_GES_PYLON_LEFT, GEM_GES_FOOT_RIGHT, GEM_GES_FOOT_LEFT, GEM_GES_UPPER_LEG_RIGHT, GEM_GES_UPPER_LEG_LEFT, GEM_GES_LOWER_LEG_RIGHT, GEM_GES_LOWER_LEG_LEFT, GEM_GES_HAND_RIGHT, GEM_GES_HAND_LEFT, GEM_GES_LOWER_ARM_RIGHT, GEM_GES_LOWER_ARM_LEFT, GEM_GES_UPPER_ARM_RIGHT, GEM_GES_UPPER_ARM_LEFT, GEM_GES_SHOULDER_RIGHT, GEM_GES_SHOULDER_LEFT, GEM_GES_HEAD, GEM_GES_HIP, GEM_GES_TORSO)
  VALUES('ed8784ed949b4006bf028b355d04ac5f', 'Master', 'GearEngineCore', 'GearEngineFuelTank', 'GearEngineRPylon', 'GearEngineLPylon', 'Jaguar_Head', 'Jaguar_Hip', 'Jaguar_Torso', 'Archetype_RightFoot', 'Archetype_LeftFoot', 'Jaguar_RightUpperLeg', 'Jaguar_LeftUpperLeg', 'Jaguar_RightLowerLeg', 'Jaguar_LeftLowerLeg', 'Archetype_RightHand', 'Archetype_LeftHand', 'Jaguar_RightLowerArm', 'Jaguar_LeftLowerArm', 'Jaguar_RightUpperArm', 'Jaguar_LeftUpperArm', 'Jaguar_RightShoulderPad', 'Jaguar_LeftShoulderPad', 'Jaguar', 0, '68ad24aab4f04c0fb7ee4a81bcf3c478', 'aa7b530f7d5b476b9d9fbaf0c34c1b6b', '1db2c0492d8b496baa8013c2fc9a2062', '19058e19aed34b38a2d735a311e3808c', '902d150932e1459aa721033f125a268b', '397c6f3edf5e450ca451e379d9c79a0a', '62ce26e5b80e41ca85fac1d44dd78750', 'f298ff2647c345d191b02d04e202fe09', '7848e24249734730a159fc8a5a921c89', 'bde509c074264813bd247bec778b0f09', '84135f9acefe436d9befa3454c571bc8', 'a84f040892f54ddcbe40ca556de96a7c', '10a2433d577140c0a4e41e177747c74c', '558e33057e4148da9e5a076d4eca56fb', '1a4b0229004d420f9ab332116623b546', 'cb36fd2c5302442190c7373c871e4bd6', 'eacdedaae74447c3a85fe232c3c7faba', '70cdce70d44b446a8c88d124e2470292', '452464df5bb94130ab03859f5e183989', '4fa535e655cd463eb87b47f7634ccffd', '3ec1e66b4f91440bb5026c5ed0a80a35');
  
INSERT INTO Tue4_SCORE_CONFIG(SCO_ID, SCO_NAME, SCO_MINIMUM_FACTOR, SCO_MAXIMUM_FACTOR, SCO_NEUTRAL_MATCHES, SCO_MONTHS_TO_CONSIDER)
  VALUES('1234567890123456789012345678912', 'DEFAULT', 0.50000, 10.00000, 30.00000, 1.00000);
INSERT INTO Tue4_EVENT_SCORE(ESC_ID, ESC_SCO_ID, ESC_EVENT_TYPE, ESC_EVENT_SCORE)
  VALUES('1234567890123456789012345678934', '1234567890123456789012345678912', 'TEAMKILL_GEAR', -250);
INSERT INTO Tue4_EVENT_SCORE(ESC_ID, ESC_SCO_ID, ESC_EVENT_TYPE, ESC_EVENT_SCORE)
  VALUES('1234567890123456789012345678945', '1234567890123456789012345678912', 'KILL_GEAR', 500);
INSERT INTO Tue4_EVENT_SCORE(ESC_ID, ESC_SCO_ID, ESC_EVENT_TYPE, ESC_EVENT_SCORE)
  VALUES('1234567890123456789012345678946', '1234567890123456789012345678912', 'TEAM_PLATE_DESTROYED', -50);
INSERT INTO Tue4_EVENT_SCORE(ESC_ID, ESC_SCO_ID, ESC_EVENT_TYPE, ESC_EVENT_SCORE)
  VALUES('1234567890123456789012345678947', '1234567890123456789012345678912', 'PLATE_DESTROYED', 100);
INSERT INTO Tue4_EVENT_SCORE(ESC_ID, ESC_SCO_ID, ESC_EVENT_TYPE, ESC_EVENT_SCORE)
  VALUES('1234567890123456789012345678948', '1234567890123456789012345678912', 'DEATHS', -100);
  
  