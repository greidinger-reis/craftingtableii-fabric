package net.johnpgr.craftingtableiifabric.network

import net.minecraft.util.Identifier

data class MessageInfo(
    val identifier: Identifier,
    val type: MessageType,
    val handler: Any
)