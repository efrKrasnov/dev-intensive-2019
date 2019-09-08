package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.*
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chatItems =
        Transformations.map(chatRepository.loadChats()) { chats ->
            return@map chats.filter { !it.isArchived }
                .map { it.toChatItem() }
                .sortedBy { it.id.toInt() }
        }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            var chats = chatItems.value!!.toMutableList()

            val chatItem = getLastArchivedChatItem()
            if(chatItem != null) {
                chats.add(0, getLastArchivedChatItem()!!)
            }

            result.value = if (queryStr.isEmpty()) chats
            else chats.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(chatItems) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }



        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

    private fun getLastChat(): Chat? {
        val chats = chatRepository.loadChats().value ?: return null
        return chats.filter { it.isArchived }.filter { it.lastMessageDate() != null }
            .maxBy { it.lastMessageDate()!! }
    }

    private fun getArchiveMessageCount(): Int {
        val chats = chatRepository.loadChats().value!!
        val archivedChats = chats.filter { it.isArchived }
        var count: Int = 0
        for (chat in archivedChats) {
            count += chat.unreadableMessageCount()
        }
        return count
    }

    private fun getLastArchivedChatItem(): ChatItem? {
        /* Получить Chat */
        val chat = getLastChat() ?: return null

        /* Вытащить все данные */
        val id = chat.id
        val shortDescription = chat.lastMessageShort()
        val messageCount = getArchiveMessageCount()
        val lastMessageDate = chat.lastMessageDate()
        val chatType = ChatType.ARCHIVE
        val isOnline = chat.messages.last().from.isOnline
        /* Построить данные для ChatItem */
        return ChatItem(
            id,
            "",
            "",
            "Архив чатов",
            shortDescription.first,
            messageCount,
            lastMessageDate!!.shortFormat(),
            isOnline,
            chatType,
            shortDescription.second
        )
    }
}