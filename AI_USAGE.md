## SIS4: AI Usage for MVVM

For SIS4, I used ChatGPT to understand MVVM architecture because I was not familiar with it before.

AI helped me understand the difference between View, ViewModel, and Repository. It also helped me work with StateFlow, UiState, and the Android lifecycle.

### Example Prompts

**Prompt 4:** "What is MVVM, and how can I use it in my PawPal application?"

This helped me understand how to separate UI, logic, and data.

**Prompt 5:** "How can I use StateFlow and UiState to manage search, filtering, and loading states?"

This helped me move the home screen logic into HomeViewModel.

**Prompt 6:** "Why does my Simulate Loading Error button not work?"

The button was visible, but nothing happened when I clicked it.

### AI Error and Fix in SIS4

When I added the Simulate Loading Error button, the callback was not connected correctly.

HomeScreen had an `onSimulateError` callback, but HomeRoute did not pass the ViewModel function to it.

I fixed this by connecting `homeViewModel::simulateError` in HomeRoute. I also connected `homeViewModel::retryAfterError` to the Retry button.

After that, I tested both buttons in the emulator, and they worked.

### What I Tested

I tested searching, filtering, sorting, loading, empty state, error state, and Retry.

I also rotated the emulator to check if the search text and selected filter would stay the same. They did, because the state was kept in HomeViewModel.

This task helped me understand why it is better to keep screen logic in the ViewModel instead of putting everything inside composable functions.