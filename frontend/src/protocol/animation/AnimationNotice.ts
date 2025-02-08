import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import AnimationImage from './AnimationImage';


class AnimationNotice {
    nonce: string = '';
    originImageUrl: string = '';
    originImageUrlCompression: string = '';
    images: Array<AnimationImage> = [];
    type: string = '';
}

export class AnimationNoticeRegistration implements IProtocolRegistration<AnimationNotice> {
    protocolId(): number {
        return 1212;
    }

    write(buffer: IByteBuffer, packet: AnimationNotice | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.images, 1200);
        buffer.writeString(packet.nonce);
        buffer.writeString(packet.originImageUrl);
        buffer.writeString(packet.originImageUrlCompression);
        buffer.writeString(packet.type);
    }

    read(buffer: IByteBuffer): AnimationNotice | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new AnimationNotice();
        const list0 = buffer.readPacketList(1200);
        packet.images = list0;
        const result1 = buffer.readString();
        packet.nonce = result1;
        const result2 = buffer.readString();
        packet.originImageUrl = result2;
        const result3 = buffer.readString();
        packet.originImageUrlCompression = result3;
        const result4 = buffer.readString();
        packet.type = result4;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default AnimationNotice;