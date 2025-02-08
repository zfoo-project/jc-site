import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class UpdateUserProfileRequest {
    name: string = '';
    phoneNumber: number = 0;
}

export class UpdateUserProfileRequestRegistration implements IProtocolRegistration<UpdateUserProfileRequest> {
    protocolId(): number {
        return 752;
    }

    write(buffer: IByteBuffer, packet: UpdateUserProfileRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.name);
        buffer.writeLong(packet.phoneNumber);
    }

    read(buffer: IByteBuffer): UpdateUserProfileRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new UpdateUserProfileRequest();
        const result0 = buffer.readString();
        packet.name = result0;
        const result1 = buffer.readLong();
        packet.phoneNumber = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default UpdateUserProfileRequest;