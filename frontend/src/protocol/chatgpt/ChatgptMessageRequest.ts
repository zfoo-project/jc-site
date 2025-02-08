import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import ChatgptMessage from './ChatgptMessage';


class ChatgptMessageRequest {
    requestId: number = 0;
    ai: number = 0;
    mobile: boolean = false;
    messages: Array<ChatgptMessage> = [];
    // 不需要哪些AI
    ignoreAIs: Set<number> = new Set();
    // google联网搜索
    googleSearch: boolean = false;
    // bing联网搜索
    bingSearch: boolean = false;
    // bilibili联网搜索
    bilibiliSearch: boolean = false;
    // 微信联网搜索
    weixinSearch: boolean = false;
}

export class ChatgptMessageRequestRegistration implements IProtocolRegistration<ChatgptMessageRequest> {
    protocolId(): number {
        return 500;
    }

    write(buffer: IByteBuffer, packet: ChatgptMessageRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.ai);
        buffer.writeBool(packet.bilibiliSearch);
        buffer.writeBool(packet.bingSearch);
        buffer.writeBool(packet.googleSearch);
        buffer.writeIntSet(packet.ignoreAIs);
        buffer.writePacketList(packet.messages, 504);
        buffer.writeBool(packet.mobile);
        buffer.writeLong(packet.requestId);
        buffer.writeBool(packet.weixinSearch);
    }

    read(buffer: IByteBuffer): ChatgptMessageRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ChatgptMessageRequest();
        const result0 = buffer.readInt();
        packet.ai = result0;
        const result1 = buffer.readBool(); 
        packet.bilibiliSearch = result1;
        const result2 = buffer.readBool(); 
        packet.bingSearch = result2;
        const result3 = buffer.readBool(); 
        packet.googleSearch = result3;
        const set4 = buffer.readIntSet();
        packet.ignoreAIs = set4;
        const list5 = buffer.readPacketList(504);
        packet.messages = list5;
        const result6 = buffer.readBool(); 
        packet.mobile = result6;
        const result7 = buffer.readLong();
        packet.requestId = result7;
        const result8 = buffer.readBool(); 
        packet.weixinSearch = result8;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ChatgptMessageRequest;