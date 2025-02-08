import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import ChatgptMessageNotice from './ChatgptMessageNotice';


class TransferChatgptNotify {
    requestSid: number = 0;
    notice: ChatgptMessageNotice | null = null;
}

export class TransferChatgptNotifyRegistration implements IProtocolRegistration<TransferChatgptNotify> {
    protocolId(): number {
        return 511;
    }

    write(buffer: IByteBuffer, packet: TransferChatgptNotify | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.notice, 501);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferChatgptNotify | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferChatgptNotify();
        const result0 = buffer.readPacket(501);
        packet.notice = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferChatgptNotify;