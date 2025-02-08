import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class GroupChatRequest {
    groupId: number = 0;
    // 0为普通聊天，1为Stable Diffusion，2为Midjourney
    type: number = 0;
    message: string = '';
}

export class GroupChatRequestRegistration implements IProtocolRegistration<GroupChatRequest> {
    protocolId(): number {
        return 602;
    }

    write(buffer: IByteBuffer, packet: GroupChatRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.groupId);
        buffer.writeString(packet.message);
        buffer.writeByte(packet.type);
    }

    read(buffer: IByteBuffer): GroupChatRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new GroupChatRequest();
        const result0 = buffer.readLong();
        packet.groupId = result0;
        const result1 = buffer.readString();
        packet.message = result1;
        const result2 = buffer.readByte();
        packet.type = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default GroupChatRequest;