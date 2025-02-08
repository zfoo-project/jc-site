import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class AnimationImage {
    imageUrl: string = '';
    imageUrlLow: string = '';
}

export class AnimationImageRegistration implements IProtocolRegistration<AnimationImage> {
    protocolId(): number {
        return 1200;
    }

    write(buffer: IByteBuffer, packet: AnimationImage | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.imageUrl);
        buffer.writeString(packet.imageUrlLow);
    }

    read(buffer: IByteBuffer): AnimationImage | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new AnimationImage();
        const result0 = buffer.readString();
        packet.imageUrl = result0;
        const result1 = buffer.readString();
        packet.imageUrlLow = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default AnimationImage;