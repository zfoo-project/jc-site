import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import SdImage from './SdImage';


class SdSimulateNotice {
    nonce: number = 0;
    images: Array<SdImage> = [];
}

export class SdSimulateNoticeRegistration implements IProtocolRegistration<SdSimulateNotice> {
    protocolId(): number {
        return 1051;
    }

    write(buffer: IByteBuffer, packet: SdSimulateNotice | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.images, 1054);
        buffer.writeLong(packet.nonce);
    }

    read(buffer: IByteBuffer): SdSimulateNotice | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SdSimulateNotice();
        const list0 = buffer.readPacketList(1054);
        packet.images = list0;
        const result1 = buffer.readLong();
        packet.nonce = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SdSimulateNotice;