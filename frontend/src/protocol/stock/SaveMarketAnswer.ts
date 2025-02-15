import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class SaveMarketAnswer {
    message: string = '';
}

export class SaveMarketAnswerRegistration implements IProtocolRegistration<SaveMarketAnswer> {
    protocolId(): number {
        return 302;
    }

    write(buffer: IByteBuffer, packet: SaveMarketAnswer | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.message);
    }

    read(buffer: IByteBuffer): SaveMarketAnswer | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SaveMarketAnswer();
        const result0 = buffer.readString();
        packet.message = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SaveMarketAnswer;