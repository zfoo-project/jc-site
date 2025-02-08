import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class ImageDownloadRequest {
    url: string = '';
}

export class ImageDownloadRequestRegistration implements IProtocolRegistration<ImageDownloadRequest> {
    protocolId(): number {
        return 1055;
    }

    write(buffer: IByteBuffer, packet: ImageDownloadRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.url);
    }

    read(buffer: IByteBuffer): ImageDownloadRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ImageDownloadRequest();
        const result0 = buffer.readString();
        packet.url = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ImageDownloadRequest;