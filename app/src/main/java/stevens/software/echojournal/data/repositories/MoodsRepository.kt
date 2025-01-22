package stevens.software.echojournal.data.repositories

import stevens.software.echojournal.R
import stevens.software.echojournal.ui.create_journal.Mood
import stevens.software.echojournal.ui.journal_entries.EntryMood

class MoodsRepository {

    fun getAllMoods() : List<EntryMood> {
        return listOf(
            EntryMood(
                id = Mood.EXCITED,
                text = R.string.entries_mood_excited,
                moodIcon = R.drawable.excited_mood
            ),
            EntryMood(
                id = Mood.PEACEFUL,
                text = R.string.entries_mood_peaceful,
                moodIcon = R.drawable.peaceful_mood
            ),
            EntryMood(
                id = Mood.NEUTRAL,
                text = R.string.entries_mood_neutral,
                moodIcon = R.drawable.neutral_mood
            ),
            EntryMood(
                id = Mood.SAD,
                text = R.string.entries_mood_sad,
                moodIcon = R.drawable.sad_mood
            ),
            EntryMood(
                id = Mood.STRESSED,
                text = R.string.entries_mood_stressed,
                moodIcon = R.drawable.stressed_mood
            ),
        )
    }

    fun toEntryMood(mood: Mood) : EntryMood  {
        when(mood){
            Mood.EXCITED -> {
                return EntryMood(
                    id = Mood.EXCITED,
                    text = R.string.entries_mood_excited,
                    moodIcon = R.drawable.selected_excited_mood
                )
            }
            Mood.PEACEFUL -> {
                return EntryMood(
                    id = Mood.PEACEFUL,
                    text = R.string.entries_mood_peaceful,
                    moodIcon = R.drawable.selected_peaceful_mood
                )
            }
            Mood.NEUTRAL -> {
                return  EntryMood(
                    id = Mood.NEUTRAL,
                    text = R.string.entries_mood_neutral,
                    moodIcon = R.drawable.selected_neutral_mood
                )
            }
            Mood.SAD -> {
                return EntryMood(
                    id = Mood.SAD,
                    text = R.string.entries_mood_sad,
                    moodIcon = R.drawable.selected_sad_mood
                )
            }
            Mood.STRESSED -> {
                return EntryMood(
                    id = Mood.STRESSED,
                    text = R.string.entries_mood_stressed,
                    moodIcon = R.drawable.selected_stressed_mood
                )
            }
            Mood.NONE -> { // todo - remove none
                return EntryMood(
                    id = Mood.NONE,
                    text = R.string.entries_mood_excited,
                    moodIcon = R.drawable.selected_excited_mood
                )
            }
        }
    }
}