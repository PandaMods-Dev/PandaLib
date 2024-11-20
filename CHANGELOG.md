# Version 0.5 Beta

## User Changelog
It seems like there is nothing new.


## Developer Changelog
### Networking API
* Added a new event `NetworkingEvents$PACKET_PAYLOAD_REGISTRY` for registering packets
* Added `PacketDistributor` class for sending packets to players and server.

### Config API
* Converting json elements into nbt tags when sending them over via packets

### Notes
* Replaced the old config codecs with a new `ConfigCodec` class
* Added a new util class `NBTUtils` which contains conversion functions for converting between nbt tags and gson json elements
* Renamed the `MathUtils` methods `rotateByPivot` to `rotateAroundOrigin`

### License Update
* Reverted license to GNU LGPLv3 (previously GNU GPLv3)