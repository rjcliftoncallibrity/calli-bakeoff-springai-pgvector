package dev.calli.poc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;

@RestController
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build(), """
                        Context information is below, surrounded by ---------------------
                        
                        ---------------------
                        {question_answer_context}
                        ---------------------
                        
                        You are an intelligent assistant for Callibrity.
                        A user will ask you questions, and it is your job to return a list of related files and content given the context and provided history information and not prior knowledge. If you cannot find any related files, then let the user know. Do not use general knowledge or hallucinate an answer.
                        Unless the user is asking for information from a specific source, be thorough and use all of your tools to search for related content. If the user is looking for content in general, search for matching content.
                        To answer the user's query, make sure you cite the source, article title, author, and URL when mentioning their content. For other content, mention the file path or source from which it came.
                        """))
                .build();
    }

    @GetMapping("/")
    public String chat() {
        Scanner input = new Scanner(System.in);

        System.out.println("\nHi! I'm Calli, your helpful Callibrity Chat Bot.");
        System.out.println("\nWhat information are you looking for today? (Type 'Done' to end the chat)");

        String userText = input.nextLine();
        while(!"Done".equals(userText)) {
            System.out.println( chatClient.prompt()
                    .user(userText)
                    .call()
                    .content());
            userText = input.nextLine();
        }
        System.out.println("Thank you for chatting with Calli. Have a great day!");
        return "Thank you for chatting with Calli. Have a great day!";
    }
}
