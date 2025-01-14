package stevens.software.echojournal.data.repositories

import stevens.software.echojournal.R
import stevens.software.echojournal.ui.create_journal.Mood
import stevens.software.echojournal.ui.journal_entries.EntryMood

class MoodsRepository {

    fun getFilterMoods() : List<EntryMood> {
        return listOf(
            EntryMood(
                text = R.string.entries_mood_excited,
                moodIcon = R.drawable.excited_mood
            ),
            EntryMood(
                text = R.string.entries_mood_peaceful,
                moodIcon = R.drawable.peaceful_mood
            ),
            EntryMood(
                text = R.string.entries_mood_neutral,
                moodIcon = R.drawable.neutral_mood
            ),
            EntryMood(
                text = R.string.entries_mood_sad,
                moodIcon = R.drawable.sad_mood
            ),
            EntryMood(
                text = R.string.entries_mood_stressed,
                moodIcon = R.drawable.stressed_mood
            ),
        )
    }

    fun toEntryMood(mood: Mood) : EntryMood  {
        when(mood){
            Mood.EXCITED -> {
                return EntryMood(
                    text = R.string.entries_mood_excited,
                    moodIcon = R.drawable.selected_excited_mood
                )
            }
            Mood.PEACEFUL -> {
                return EntryMood(
                    text = R.string.entries_mood_peaceful,
                    moodIcon = R.drawable.selected_peaceful_mood
                )
            }
            Mood.NEUTRAL -> {
                return  EntryMood(
                    text = R.string.entries_mood_neutral,
                    moodIcon = R.drawable.selected_neutral_mood
                )
            }
            Mood.SAD -> {
                return EntryMood(
                    text = R.string.entries_mood_sad,
                    moodIcon = R.drawable.selected_sad_mood
                )
            }
            Mood.STRESSED -> {
                return EntryMood(
                    text = R.string.entries_mood_stressed,
                    moodIcon = R.drawable.selected_stressed_mood
                )
            }
            Mood.NONE -> { // todo - remove none
                return EntryMood(
                    text = R.string.entries_mood_excited,
                    moodIcon = R.drawable.selected_excited_mood
                )
            }
        }
    }
}