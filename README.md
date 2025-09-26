<img width="1085" height="522" alt="proof_of_concept_logo" src="https://github.com/user-attachments/assets/823c7dbb-c98f-4b1d-ae1a-e0df9268a7f1" />

Memory Foam
======
A simple mod that adds a potion effect whenever you sleep in a specific type of bed. This mod was the result of a mod request.  
Mainly intended to be used by modpack devs and server owners.  
Ports to other modloaders and MC versions soon.


### Installation and info  
This mod has a config stored as a memoryfoamconfig.toml file. Said file has comments for specific instructions on how to configure the mod for beds and effects.  
The mod can be configured to give players effects when they sleep in a bed. This supports effects that use MobEffectInstance to apply to a player, and beds that extend the BedBlock class.  
Memory Foam's config system very much assumes that you are able to look at other installed mods' source code for class names and resourcelocations.  
It is also possible to create an add-on ontop of MemoryFoam that adds your own custom set of bed type to effect associations if the config is insufficient.  

Memory Foam works exclusively on the server side, and creates a new config for every new world file, and requires a world restart before a config changes.
