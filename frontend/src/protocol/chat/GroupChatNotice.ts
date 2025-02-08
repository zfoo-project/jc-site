import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import ChatMessage from './ChatMessage';


class GroupChatNotice {
    messages: Array<ChatMessage> = [];
}

export class GroupChatNoticeRegistration implements IProtocolRegistration<GroupChatNotice> {
    protocolId(): number {
        return 601;
    }

    write(buffer: IByteBuffer, packet: GroupChatNotice | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.messages, 600);
    }

    read(buffer: IByteBuffer): GroupChatNotice | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new GroupChatNotice();
        const list0 = buffer.readPacketList(600);
        packet.messages = list0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default GroupChatNotice;